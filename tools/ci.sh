#!/usr/bin/env bash
# سكربت البناء المستمر — يستدعيه سير العمل في GitHub Actions (tools/ci-workflow.yml).
#
# الهدف: احتواء كل منطق البناء هنا حتى يمكن تعديله بسهولة دون لمس ملف
# .github/workflows (الذي يحتاج صلاحيات خاصة لتعديله).
#
# الخطوات:
#   1) تهيئة حزم Android SDK المطلوبة (المشغّلات تسبق-install الحزمة الأساسية).
#   2) بناء الـ APK (assembleDebug) وتشغيل اختبارات الوحدات (test).
#   3) نشر الـ APK في Releases بعنوان "prototype".
set -euo pipefail
cd "$(dirname "$0")/.."

echo "==> [1/3] تهيئة Android SDK"
SDK_ROOT="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-/usr/local/lib/android/sdk}}"
SDKMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"
if [[ ! -x "$SDKMANAGER" ]]; then
  SDKMANAGER="$(command -v sdkmanager)"
fi
if [[ -z "${SDKMANAGER:-}" ]] || [[ ! -x "$SDKMANAGER" ]]; then
  echo "!! لم يُعثر على sdkmanager" >&2
  exit 1
fi
echo "    sdkmanager: $SDKMANAGER"
yes | "$SDKMANAGER" --licenses > /dev/null || true
"$SDKMANAGER" --install "platforms;android-35" "build-tools;35.0.0" > /dev/null
echo "    تم."

echo "==> [2/3] بناء الـ APK وتشغيل اختبارات الوحدات"
./gradlew --no-daemon --stacktrace assembleDebug test
echo "    APK: app/build/outputs/apk/debug/app-debug.apk"

echo "==> [3/3] نشر نسخة Prototype في Releases"
if [[ -n "${GH_TOKEN:-}" ]]; then
  gh release delete prototype --cleanup-tag -y || true
  gh release create prototype app/build/outputs/apk/debug/app-debug.apk \
    --title "Prototype — المساعد الدراسي الذكي" \
    --notes "نسخة تجريبية (debug APK) مبنية تلقائيًا من أحدث كود في الفرع." \
    --prerelease \
    --target "$GITHUB_SHA"
  echo "    تم النشر: https://github.com/${GITHUB_REPOSITORY}/releases/tag/prototype"
else
  echo "    GH_TOKEN غير متوفر — تخطّي النشر."
fi

echo "==> اكتمل بنجاح ✅"

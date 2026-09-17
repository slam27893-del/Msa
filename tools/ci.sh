#!/usr/bin/env bash
# سكربت البناء المستمر — يستدعيه سير العمل في GitHub Actions (tools/ci-workflow.yml).
#
# الهدف: احتواء كل منطق البناء هنا حتى يمكن تعديله بسهولة دون لمس ملف
# .github/workflows (الذي يحتاج صلاحيات خاصة لتعديله).
#
# الخطوات:
#   1) التحقق من حزم Android SDK المطلوبة (المشغّلات تثبت أغلبها مسبقًا).
#   2) بناء الـ APK (assembleDebug) وتشغيل اختبارات الوحدات (test).
#   3) نشر الـ APK في Releases بعنوان "prototype".
#
# عند أي فشل: يُعرض ملخص الخطأ كـ Annotations، ويُنشر سجل الأخطاء كاملًا
# في release باسم "build-log" لتسهيل التشخيص عن بُعد.
set -euo pipefail
cd "$(dirname "$0")/.."

BUILD_LOG="$(mktemp /tmp/msa-build.XXXXXX.log)"
exec > >(tee "$BUILD_LOG") 2>&1

cleanup() {
  code=$?
  if [[ $code -ne 0 ]]; then
    publish_error_release || true
  fi
  exit $code
}
trap cleanup EXIT

publish_error_release() {
  # Annotations تظهر مباشرة في واجهة GitHub
  grep -E "^e: |error: |FAILURE|What went wrong|Caused by" "$BUILD_LOG" 2>/dev/null | head -8 | while IFS= read -r line; do
    printf '::error::%s\n' "$(echo "$line" | cut -c1-240 | tr -d '\r')" || true
  done
  # قناة موثوقة للتشخيص: release مؤقت يحمل مقتطف السجل في الوصف
  if [[ -n "${GH_TOKEN:-}" ]] && command -v gh >/dev/null 2>&1; then
    echo "==> نشر سجل الأخطاء في release مؤقت (build-log)"
    gh release delete build-log --cleanup-tag -y >/dev/null 2>&1 || true
    {
      echo "آخر 500 سطر من سجل البناء الفاشل:"
      echo ""
      tail -n 500 "$BUILD_LOG"
    } > /tmp/msa-error-snippet.txt
    gh release create build-log /tmp/msa-error-snippet.txt \
      --title "سجل آخر بناء فاشل (تشخيص)" \
      --notes-file /tmp/msa-error-snippet.txt \
      --prerelease \
      --target "$GITHUB_SHA" >/dev/null 2>&1 || true
  fi
}

echo "==> [1/3] التحقق من Android SDK"
SDK_ROOT="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-/usr/local/lib/android/sdk}}"
echo "    SDK_ROOT=$SDK_ROOT"
echo "    java: $(java -version 2>&1 | head -1)"

need_install=0
if [[ ! -d "$SDK_ROOT/platforms/android-35" ]]; then
  echo "    منصة android-35 غير مثبتة — سيتم تثبيتها."
  need_install=1
fi
if [[ ! -d "$SDK_ROOT/build-tools/35.0.0" ]]; then
  echo "    build-tools 35.0.0 غير مثبتة — سيتم تثبيتها."
  need_install=1
fi

if [[ $need_install -eq 1 ]]; then
  SDKMANAGER=""
  if [[ -x "$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]]; then
    SDKMANAGER="$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager"
  elif [[ -x "$SDK_ROOT/cmdline-tools/bin/sdkmanager" ]]; then
    SDKMANAGER="$SDK_ROOT/cmdline-tools/bin/sdkmanager"
  else
    SDKMANAGER="$(command -v sdkmanager || true)"
  fi
  if [[ -z "$SDKMANAGER" ]]; then
    echo "!! لم يُعثر على sdkmanager — محتويات SDK:" >&2
    ls -la "$SDK_ROOT" || true
    ls -la "$SDK_ROOT/cmdline-tools" || true
    exit 1
  fi
  echo "    sdkmanager: $SDKMANAGER"
  yes | "$SDKMANAGER" --licenses >/dev/null || true
  "$SDKMANAGER" --install "platforms;android-35" "build-tools;35.0.0" || {
    echo "!! فشل تثبيت حزم SDK" >&2
    exit 1
  }
else
  echo "    كل الحزم المطلوبة موجودة مسبقًا."
fi

echo "==> [2/3] بناء الـ APK وتشغيل اختبارات الوحدات"
./gradlew --no-daemon --stacktrace --console=plain assembleDebug test
echo "    APK: $(ls -la app/build/outputs/apk/debug/app-debug.apk)"

echo "==> [3/3] نشر نسخة Prototype في Releases"
if [[ -n "${GH_TOKEN:-}" ]]; then
  # إزالة release التشخيص القديم إن وجد (البناء نجح)
  gh release delete build-log --cleanup-tag -y >/dev/null 2>&1 || true
  gh release delete prototype --cleanup-tag -y >/dev/null 2>&1 || true
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

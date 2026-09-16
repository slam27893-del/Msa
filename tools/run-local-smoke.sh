#!/usr/bin/env bash
# اختبار محلي لمنطق التقدم بدون Gradle / Android SDK.
# المتطلبات: JDK 17 و kotlinc (مترجم كوتلن) في المسار أو عبر متغير KOTLINC.
# الاستخدام:  tools/run-local-smoke.sh
set -euo pipefail
cd "$(dirname "$0")/.."

KOTLINC_BIN="${KOTLINC:-kotlinc}"
OUT="$(mktemp -d)"
trap 'rm -rf "$OUT"' EXIT

SOURCES=$(find core/model/src/main/kotlin core/curriculum/src/main/kotlin core/domain/src/main/kotlin tools/local-smoke/stubs -name '*.kt')

# shellcheck disable=SC2086
"$KOTLINC_BIN" $SOURCES tools/local-smoke/LocalSmokeTest.kt -include-runtime -d "$OUT/smoke.jar"
java -jar "$OUT/smoke.jar"

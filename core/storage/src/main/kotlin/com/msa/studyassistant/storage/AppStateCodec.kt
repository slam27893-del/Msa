package com.msa.studyassistant.storage

import com.msa.studyassistant.model.AppState
import kotlinx.serialization.json.Json

/**
 * تحويل حالة التطبيق من/إلى JSON (kotlinx.serialization).
 * هذا هو "الشكل المحفوظ" للبيانات على الجهاز.
 */
class AppStateCodec(private val json: Json = DefaultJson) {

    fun encode(state: AppState): String = json.encodeToString(AppState.serializer(), state)

    /** يعيد null إذا لم توجد بيانات أو كانت تالفة (بدل تعطّل التطبيق). */
    fun decode(raw: String?): AppState? =
        raw?.let { runCatching { json.decodeFromString(AppState.serializer(), it) }.getOrNull() }

    companion object {
        val DefaultJson: Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
}

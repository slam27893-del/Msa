package com.msa.studyassistant.storage

/**
 * واجهة تخزين محلي بسيط (مفتاح/قيمة).
 *
 * في Android يتم تنفيذها بـ SharedPreferences (انظر app/.../SharedPreferencesKeyValueStore)،
 * وفي الاختبارات بـ [InMemoryKeyValueStore]. ولاحقًا يمكن استبدالها بـ DataStore أو Room
 * دون تغيير أي منطق أعلى منها.
 */
interface KeyValueStore {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
}

/** تنفيذ في الذاكرة، للاختبارات. */
class InMemoryKeyValueStore : KeyValueStore {
    private val map = mutableMapOf<String, String>()
    override fun getString(key: String): String? = map[key]
    override fun putString(key: String, value: String) {
        map[key] = value
    }
}

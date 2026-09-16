package com.msa.studyassistant.data

import android.content.SharedPreferences
import com.msa.studyassistant.storage.KeyValueStore

/**
 * تنفيذ [KeyValueStore] فوق SharedPreferences في أندرويد.
 * (commit() وليس apply() حتى لا تضيع آخر كتابة إذا أُغلق التطبيق فجأة).
 */
class SharedPreferencesKeyValueStore(
    private val prefs: SharedPreferences,
) : KeyValueStore {
    override fun getString(key: String): String? = prefs.getString(key, null)
    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).commit()
    }
}

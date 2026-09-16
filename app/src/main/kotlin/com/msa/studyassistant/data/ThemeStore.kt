package com.msa.studyassistant.data

import com.msa.studyassistant.storage.KeyValueStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** وضع المظهر: تلقائي (حسب النظام) أو فاتح أو داكن. */
enum class ThemeMode(val label: String) {
    SYSTEM("تلقائي (حسب النظام)"),
    LIGHT("فاتح"),
    DARK("داكن"),
}

/** حفظ واسترجاع تفضيل المظهر محليًا حتى يبقى بعد إعادة فتح التطبيق. */
class ThemeStore(private val store: KeyValueStore) {

    private val _mode = MutableStateFlow(
        store.getString(KEY)?.let { saved -> runCatching { ThemeMode.valueOf(saved) }.getOrNull() } ?: ThemeMode.SYSTEM,
    )
    val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

    fun setMode(mode: ThemeMode) {
        _mode.value = mode
        store.putString(KEY, mode.name)
    }

    companion object {
        private const val KEY = "theme_mode"
    }
}

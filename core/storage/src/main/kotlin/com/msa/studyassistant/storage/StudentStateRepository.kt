package com.msa.studyassistant.storage

import com.msa.studyassistant.model.AppState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * مستودع حالة الطالب: التحميل من التخزين المحلي عند الإنشاء،
 * والحفظ تلقائيًا مع كل تعديل — وهكذا تبقى البيانات بعد إغلاق التطبيق.
 */
class StudentStateRepository(
    private val store: KeyValueStore,
    private val codec: AppStateCodec = AppStateCodec(),
) {
    private val _state = MutableStateFlow(codec.decode(store.getString(KEY_APP_STATE)) ?: AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    /** تعديل الحالة (دالة تحويل نقية) وحفظها فورًا في التخزين المحلي. */
    fun update(transform: (AppState) -> AppState) {
        val next = transform(_state.value)
        if (next != _state.value) {
            _state.value = next
            store.putString(KEY_APP_STATE, codec.encode(next))
        }
    }

    companion object {
        const val KEY_APP_STATE = "app_state"
    }
}

// PROOF OF CONCEPT - MOCK DATA
// درجات الطالب المُدخلة يدويًا (محاكاة نظام نور) — بيانات تجريبية محلية فقط.
// لا يوجد أي ربط حقيقي بنظام نور أو أي نظام خارجي؛ هذا تخزين محلي تمهيدًا
// لربط رسمي مستقبلي عند توفر التصريح.

package com.msa.studyassistant.data

import com.msa.studyassistant.storage.KeyValueStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * مخزن الدرجات اليدوية (لكل مادة: 0..100 أو غير مُدخلة).
 * يُحفظ محليًا في نفس مخزن التطبيق تحت مفتاح مستقل.
 */
class GradesStore(private val store: KeyValueStore) {

    @Serializable
    private data class StoredGrade(val subjectId: String, val grade: Int)

    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = ListSerializer(StoredGrade.serializer())

    private val _grades = MutableStateFlow(load())
    val grades: StateFlow<Map<String, Int>> = _grades.asStateFlow()

    /** تحديث درجة مادة (null = مسح الدرجة). */
    fun setGrade(subjectId: String, grade: Int?) {
        val current = _grades.value
        val next = if (grade == null) {
            current - subjectId
        } else {
            current + (subjectId to grade.coerceIn(0, 100))
        }
        if (next != current) {
            _grades.value = next
            store.putString(KEY_GRADES, json.encodeToString(serializer, next.map { StoredGrade(it.key, it.value) }))
        }
    }

    private fun load(): Map<String, Int> =
        runCatching {
            json.decodeFromString(serializer, store.getString(KEY_GRADES) ?: "[]")
                .associate { it.subjectId to it.grade }
        }.getOrDefault(emptyMap())

    companion object {
        private const val KEY_GRADES = "poc_grades"
    }
}

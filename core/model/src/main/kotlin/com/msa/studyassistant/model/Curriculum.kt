package com.msa.studyassistant.model

/**
 * نماذج المنهج الدراسي: مادة ← وحدات ← دروس مرتبة.
 *
 * هذه النماذج مستقلة تمامًا عن الواجهة وعن آلية التخزين؛
 * بيانات المنهج الفعلية تأتي من [com.msa.studyassistant.curriculum.CurriculumDataSource].
 */

data class CurriculumLesson(
    val id: String,
    val title: String,
    val objectives: List<String> = emptyList(),
    val keyConcepts: List<String> = emptyList(),
    val summary: String = "",
)

data class CurriculumUnit(
    val id: String,
    val title: String,
    val lessons: List<CurriculumLesson> = emptyList(),
)

data class CurriculumSubject(
    val id: String,
    val name: String,
    val units: List<CurriculumUnit> = emptyList(),
) {
    /** جميع الدروس بالترتيب التسلسلي عبر الوحدات (الأساس لفهم تسلسل المنهج). */
    val lessons: List<CurriculumLesson> by lazy { units.flatMap { it.lessons } }

    val totalLessons: Int get() = lessons.size

    fun lessonAt(index: Int): CurriculumLesson? = lessons.getOrNull(index)

    /** الوحدة التي يقع فيها الدرس ذو الرقم التسلسلي المعطى. */
    fun unitForGlobalIndex(index: Int): CurriculumUnit? {
        var offset = 0
        for (unit in units) {
            if (index < offset + unit.lessons.size) return unit
            offset += unit.lessons.size
        }
        return null
    }

    /** درس + وحدته + رقمه التسلسلي الكلي، لتسهيل عرض قوائم المنهج في الواجهة. */
    val lessonEntries: List<LessonEntry> by lazy {
        buildList {
            var index = 0
            for (unit in units) {
                for (lesson in unit.lessons) {
                    add(LessonEntry(globalIndex = index, lesson = lesson, unit = unit))
                    index++
                }
            }
        }
    }
}

data class LessonEntry(
    val globalIndex: Int,
    val lesson: CurriculumLesson,
    val unit: CurriculumUnit,
)

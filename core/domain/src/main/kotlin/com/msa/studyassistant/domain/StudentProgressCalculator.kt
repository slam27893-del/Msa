package com.msa.studyassistant.domain

import com.msa.studyassistant.model.AttendanceChoice
import com.msa.studyassistant.model.CurriculumSubject
import com.msa.studyassistant.model.StudentEvent
import kotlin.math.abs

/**
 * محرك التقدم: يحول سجل الأحداث إلى حالة تقدم كاملة.
 *
 * القواعد الأساسية (كما في مواصفات Prototype):
 *  - "تم أخذ الدرس"   ← ينتقل الدرس الحالي إلى الدرس التالي.
 *  - "لم نأخذ الدرس"  ← لا يتغير الدرس الحالي.
 *  - "غياب"           ← لا يتغير الدرس الحالي + تسجيل الدرس ضمن الفائتة.
 *                       (غياب الطالب لا يعني أن الدرس أُخذ)
 *  - الدروس المكتملة هي كل الدروس قبل الدرس الحالي.
 *  - الدرس الفائت يبقى "فائتًا" حتى يتجاوزه الدرس الحالي (أي يُسجَّل أُخذ لاحقًا).
 */
class StudentProgressCalculator(
    /** الفرق الذي يُعتبر "كبيرًا" عند الاختيار اليدوي للدرس ويستدعي تأكيدًا. */
    private val significantJumpThreshold: Int = DEFAULT_SIGNIFICANT_JUMP_THRESHOLD,
) {

    fun calculate(curriculum: List<CurriculumSubject>, events: List<StudentEvent>): StudentProgress {
        val totals = curriculum.associate { it.id to it.totalLessons }
        val current = mutableMapOf<String, Int>()
        val missedEntries = mutableListOf<MissedRecord>()

        for (event in events) {
            when (event) {
                is StudentEvent.DayAttendance -> {
                    for (entry in event.entries) {
                        val total = totals[entry.subjectId] ?: continue
                        val index = current.getOrPut(entry.subjectId) { 0 }
                        when (entry.choice) {
                            AttendanceChoice.TAKEN ->
                                if (index < total) current[entry.subjectId] = index + 1

                            AttendanceChoice.NOT_TAKEN -> Unit // لا تغيير

                            AttendanceChoice.ABSENT ->
                                // الغياب لا يحرك المؤشر؛ نسجل أن هذا الدرس فاته في هذا التاريخ.
                                if (index < total) missedEntries += MissedRecord(entry.subjectId, index, event.date)
                        }
                    }
                }

                is StudentEvent.LessonsTaken -> {
                    val total = totals[event.subjectId] ?: continue
                    val maxIndex = event.lessonIndices.maxOrNull() ?: continue
                    val index = current.getOrPut(event.subjectId) { 0 }
                    if (maxIndex >= index) {
                        current[event.subjectId] = minOf(maxIndex + 1, total)
                    }
                }

                is StudentEvent.CurrentLessonSet -> {
                    val total = totals[event.subjectId] ?: continue
                    current[event.subjectId] = event.lessonIndex.coerceIn(0, total)
                }
            }
        }

        val subjectProgress = curriculum.associate { subject ->
            val index = current[subject.id] ?: 0
            val missed = missedEntries
                .filter { it.subjectId == subject.id && it.lessonIndex >= index }
                .map { it.lessonIndex }
                .toSortedSet()
            subject.id to SubjectProgress(
                subjectId = subject.id,
                currentLessonIndex = index,
                totalLessons = subject.totalLessons,
                missedLessonIndices = missed,
            )
        }

        val subjectOrder = curriculum.withIndex().associate { (i, s) -> s.id to i }
        val missedLessons = missedEntries
            .filter { it.lessonIndex >= (current[it.subjectId] ?: 0) }
            .groupBy { it.subjectId to it.lessonIndex }
            .map { (key, records) ->
                MissedLesson(
                    subjectId = key.first,
                    lessonIndex = key.second,
                    missedDates = records.map { it.date }.distinct().sorted(),
                )
            }
            .sortedWith(compareBy({ subjectOrder[it.subjectId] ?: Int.MAX_VALUE }, { it.lessonIndex }))

        return StudentProgress(subjectProgress = subjectProgress, missedLessons = missedLessons)
    }

    /**
     * هل الفرق بين الدرس الحالي والدرس المختار يدويًا "كبير" بما يستدعي تأكيدًا؟
     * يُستخدم في شاشة الدرس قبل تعيين درس بعيد كدرس حالي.
     */
    fun isSignificantJump(currentIndex: Int, targetIndex: Int): Boolean =
        abs(currentIndex - targetIndex) > significantJumpThreshold

    /** المسافة بين درسين (لعرضها في رسالة التأكيد). */
    fun jumpDistance(currentIndex: Int, targetIndex: Int): Int = abs(currentIndex - targetIndex)

    private data class MissedRecord(val subjectId: String, val lessonIndex: Int, val date: String)

    companion object {
        const val DEFAULT_SIGNIFICANT_JUMP_THRESHOLD = 2
    }
}

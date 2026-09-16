package com.msa.studyassistant.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * خيارات تسجيل حالة المادة في يوم دراسي معين.
 */
@Serializable
enum class AttendanceChoice {
    /** تم أخذ الدرس (ينتقل المؤشر للدرس التالي). */
    TAKEN,

    /** لم نأخذ الدرس (لا يتغير الدرس الحالي). */
    NOT_TAKEN,

    /** غياب (لا يتغير الدرس الحالي + يُسجل الدرس ضمن الدروس الفائتة). */
    ABSENT,
}

@Serializable
data class SubjectAttendance(
    val subjectId: String,
    val choice: AttendanceChoice,
)

/**
 * سجل أحداث الطالب — نهج "Event Sourcing" مبسّط:
 *
 * بدل تعديل حالة التقدم يدويًا في كل مرة، نسجّل أحداثًا فقط،
 * ثم نشتق التقدم دائمًا بإعادة تشغيل الأحداث من البداية.
 *
 * الميزة: تعديل تسجيل يوم سابق (مثل تغيير "أُخذ" إلى "غياب")
 * عملية آمنة ومتسقة، لأن كل شيء يُعاد حسابه من الأحداث.
 */
@Serializable
sealed interface StudentEvent {

    /** تسجيل يوم دراسي: حالة كل مادة مجدولة في ذلك التاريخ. */
    @Serializable
    @SerialName("day_attendance")
    data class DayAttendance(
        val date: String, // ISO-8601 مثل 2026-09-16
        val entries: List<SubjectAttendance> = emptyList(),
    ) : StudentEvent

    /** تعليم درس أو أكثر بأنها أُخذت (يستخدم للاستدراك والاختيار المتعدد). */
    @Serializable
    @SerialName("lessons_taken")
    data class LessonsTaken(
        val subjectId: String,
        val lessonIndices: List<Int>,
    ) : StudentEvent

    /** تعيين درس معين كدرس حالي (اختيار يدوي من قائمة المنهج). */
    @Serializable
    @SerialName("current_lesson_set")
    data class CurrentLessonSet(
        val subjectId: String,
        val lessonIndex: Int,
    ) : StudentEvent
}

package com.msa.studyassistant.model

import kotlinx.serialization.Serializable

/**
 * حالة التطبيق الكاملة المحفوظة محليًا.
 *
 * التقدم نفسه لا يُخزَّن مباشرة، بل يُشتق من [events] عبر
 * [com.msa.studyassistant.domain.StudentProgressCalculator] عند الحاجة،
 * وهو ما يضمن ثبات البيانات بعد إغلاق التطبيق وإعادة فتحه.
 */
@Serializable
data class AppState(
    val profile: StudentProfile? = null,
    val schedule: WeeklySchedule = WeeklySchedule(),
    val events: List<StudentEvent> = emptyList(),
) {
    fun withProfile(profile: StudentProfile): AppState = copy(profile = profile)

    fun withSchedule(schedule: WeeklySchedule): AppState = copy(schedule = schedule)

    fun withEvent(event: StudentEvent): AppState = copy(events = events + event)

    /**
     * تسجيل/تحديث يوم دراسي كامل.
     * يستبدل تسجيل نفس التاريخ إن وُجد (لأن كل يوم له سجل واحد).
     */
    fun withDayAttendance(date: String, entries: List<SubjectAttendance>): AppState {
        val others = events.filterNot { it is StudentEvent.DayAttendance && it.date == date }
        return copy(events = others + StudentEvent.DayAttendance(date, entries))
    }

    fun dayAttendance(date: String): StudentEvent.DayAttendance? =
        events.filterIsInstance<StudentEvent.DayAttendance>().firstOrNull { it.date == date }
}

package com.msa.studyassistant.model

import kotlinx.serialization.Serializable

/**
 * الجدول الأسبوعي: مواد كل يوم.
 * في هذه النسخة لا توجد أوقات أو أرقام حصص — فقط أي المواد موجودة في كل يوم.
 */
@Serializable
data class WeeklySchedule(
    val subjectsByDay: Map<WeekDay, List<String>> = emptyMap(),
) {
    fun subjectsOn(day: WeekDay): List<String> = subjectsByDay[day].orEmpty()

    /** إرجاع نسخة جديدة بعد تحديث مواد يوم معين. */
    fun withSubjectsForDay(day: WeekDay, subjectIds: List<String>): WeeklySchedule =
        copy(subjectsByDay = subjectsByDay + (day to subjectIds))

    fun daysContaining(subjectId: String): List<WeekDay> =
        WeekDay.entries.filter { subjectId in subjectsOn(it) }

    val isEmpty: Boolean get() = subjectsByDay.values.all { it.isEmpty() }
}

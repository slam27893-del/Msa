package com.msa.studyassistant.model

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * أيام الأسبوع بصياغة عربية مع ربطها بـ java.time.
 * الترتيب يبدأ بالأحد لأنه بداية الأسبوع الدراسي في السعودية.
 */
enum class WeekDay(val displayName: String) {
    SUNDAY("الأحد"),
    MONDAY("الاثنين"),
    TUESDAY("الثلاثاء"),
    WEDNESDAY("الأربعاء"),
    THURSDAY("الخميس"),
    FRIDAY("الجمعة"),
    SATURDAY("السبت");

    fun toJavaDayOfWeek(): DayOfWeek =
        if (ordinal == 0) DayOfWeek.SUNDAY else DayOfWeek.of(ordinal)

    companion object {
        fun fromJavaDay(day: DayOfWeek): WeekDay = entries[day.value % 7]

        fun of(date: LocalDate): WeekDay = fromJavaDay(date.dayOfWeek)
    }
}

fun LocalDate.weekDay(): WeekDay = WeekDay.fromJavaDay(dayOfWeek)

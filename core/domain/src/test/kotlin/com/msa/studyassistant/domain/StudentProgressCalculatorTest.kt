package com.msa.studyassistant.domain

import com.msa.studyassistant.curriculum.SampleCurriculum
import com.msa.studyassistant.model.AppState
import com.msa.studyassistant.model.AttendanceChoice
import com.msa.studyassistant.model.StudentEvent
import com.msa.studyassistant.model.StudentProfile
import com.msa.studyassistant.model.SubjectAttendance
import com.msa.studyassistant.model.WeekDay
import com.msa.studyassistant.model.WeeklySchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * اختبارات محرك التقدم — القواعد الأساسية للـ Prototype.
 */
class StudentProgressCalculatorTest {

    private val subjects = SampleCurriculum.subjectsFor(StudentProfile())
    private val calc = StudentProgressCalculator()

    private fun state() = AppState(
        schedule = WeeklySchedule().withSubjectsForDay(WeekDay.SUNDAY, listOf("math", "english")),
    )

    @Test
    fun `taking a lesson advances the current lesson`() {
        val state = state().withDayAttendance(
            "2026-09-13",
            listOf(SubjectAttendance("math", AttendanceChoice.TAKEN)),
        )
        val progress = calc.calculate(subjects, state.events)
        assertEquals(1, progress.subjectProgress.getValue("math").currentLessonIndex)
        assertEquals(0, progress.subjectProgress.getValue("english").currentLessonIndex)
    }

    @Test
    fun `not taking a lesson keeps the current lesson`() {
        val state = state().withDayAttendance(
            "2026-09-13",
            listOf(
                SubjectAttendance("math", AttendanceChoice.TAKEN),
                SubjectAttendance("english", AttendanceChoice.NOT_TAKEN),
            ),
        )
        val progress = calc.calculate(subjects, state.events)
        assertEquals(1, progress.subjectProgress.getValue("math").currentLessonIndex)
        assertEquals(0, progress.subjectProgress.getValue("english").currentLessonIndex)
        assertTrue(progress.missedLessons.isEmpty())
    }

    @Test
    fun `absence keeps the current lesson and records the missed lesson`() {
        val state = state().withDayAttendance(
            "2026-09-13",
            listOf(
                SubjectAttendance("math", AttendanceChoice.TAKEN),
                SubjectAttendance("english", AttendanceChoice.ABSENT),
            ),
        )
        val progress = calc.calculate(subjects, state.events)

        // الغياب لا يعني أن الدرس أُخذ
        assertEquals(0, progress.subjectProgress.getValue("english").currentLessonIndex)

        assertEquals(1, progress.missedLessons.size)
        val missed = progress.missedLessons.first()
        assertEquals("english", missed.subjectId)
        assertEquals(0, missed.lessonIndex)
        assertEquals(listOf("2026-09-13"), missed.missedDates)
    }

    @Test
    fun `attended today defaults mark all scheduled subjects as taken`() {
        val defaults = AttendanceDefaults.defaultChoices(listOf("math", "english"))
        assertTrue(defaultersAllTaken(defaults))

        val state = state().withDayAttendance(
            "2026-09-13",
            defaults.map { SubjectAttendance(it.key, it.value) },
        )
        val progress = calc.calculate(subjects, state.events)
        assertEquals(1, progress.subjectProgress.getValue("math").currentLessonIndex)
        assertEquals(1, progress.subjectProgress.getValue("english").currentLessonIndex)
        assertTrue(progress.missedLessons.isEmpty())
    }

    private fun defaultersAllTaken(map: Map<String, AttendanceChoice>) =
        map.values.all { it == AttendanceChoice.TAKEN }

    @Test
    fun `editing the same day recomputes correctly`() {
        var state = state().withDayAttendance(
            "2026-09-13",
            listOf(
                SubjectAttendance("math", AttendanceChoice.TAKEN),
                SubjectAttendance("english", AttendanceChoice.ABSENT),
            ),
        )
        var progress = calc.calculate(subjects, state.events)
        assertEquals(0, progress.subjectProgress.getValue("english").currentLessonIndex)
        assertEquals(1, progress.missedLessons.size)

        // تغيير الإنجليزي من غياب إلى أُخذ في نفس اليوم
        state = state.withDayAttendance(
            "2026-09-13",
            listOf(
                SubjectAttendance("math", AttendanceChoice.TAKEN),
                SubjectAttendance("english", AttendanceChoice.TAKEN),
            ),
        )
        progress = calc.calculate(subjects, state.events)
        assertEquals(1, progress.subjectProgress.getValue("english").currentLessonIndex)
        assertTrue(progress.missedLessons.isEmpty())
    }

    @Test
    fun `repeated absence on different days records multiple dates for the same lesson`() {
        val state = state()
            .withDayAttendance("2026-09-13", listOf(SubjectAttendance("english", AttendanceChoice.ABSENT)))
            .withDayAttendance("2026-09-20", listOf(SubjectAttendance("english", AttendanceChoice.ABSENT)))

        val progress = calc.calculate(subjects, state.events)
        assertEquals(1, progress.missedLessons.size)
        assertEquals(listOf("2026-09-13", "2026-09-20"), progress.missedLessons.first().missedDates)
    }

    @Test
    fun `marking multiple lessons as taken advances past them`() {
        val state = state().withEvent(StudentEvent.LessonsTaken("math", listOf(1, 2, 3)))
        val progress = calc.calculate(subjects, state.events)
        assertEquals(4, progress.subjectProgress.getValue("math").currentLessonIndex)
    }

    @Test
    fun `marking a missed lesson as taken later resolves it`() {
        var state = state().withDayAttendance(
            "2026-09-13",
            listOf(SubjectAttendance("english", AttendanceChoice.ABSENT)),
        )
        var progress = calc.calculate(subjects, state.events)
        assertEquals(1, progress.missedLessons.size)

        state = state.withEvent(StudentEvent.LessonsTaken("english", listOf(0)))
        progress = calc.calculate(subjects, state.events)
        assertEquals(1, progress.subjectProgress.getValue("english").currentLessonIndex)
        assertTrue(progress.missedLessons.isEmpty())
    }

    @Test
    fun `manual lesson selection works forward and backward`() {
        var state = state().withEvent(StudentEvent.CurrentLessonSet("math", 7))
        var progress = calc.calculate(subjects, state.events)
        assertEquals(7, progress.subjectProgress.getValue("math").currentLessonIndex)

        state = state.withEvent(StudentEvent.CurrentLessonSet("math", 2))
        progress = calc.calculate(subjects, state.events)
        assertEquals(2, progress.subjectProgress.getValue("math").currentLessonIndex)
    }

    @Test
    fun `manual selection out of range is clamped`() {
        val state = state().withEvent(StudentEvent.CurrentLessonSet("math", 99))
        val progress = calc.calculate(subjects, state.events)
        assertEquals(subjects.first { it.id == "math" }.totalLessons, progress.subjectProgress.getValue("math").currentLessonIndex)
    }

    @Test
    fun `significant jump detection`() {
        assertTrue(calc.isSignificantJump(2, 7))
        assertFalse(calc.isSignificantJump(2, 4))
        assertTrue(calc.isSignificantJump(7, 2))
        assertFalse(calc.isSignificantJump(0, 1))
    }

    @Test
    fun `progress percentages per subject and overall`() {
        val state = state()
            .withDayAttendance("2026-09-13", listOf(SubjectAttendance("math", AttendanceChoice.TAKEN)))
            .withEvent(StudentEvent.LessonsTaken("english", listOf(0)))

        val progress = calc.calculate(subjects, state.events)

        val math = progress.subjectProgress.getValue("math")
        assertEquals(1, math.completedLessons)
        assertEquals(9, math.totalLessons)
        assertEquals(11, math.percent)

        // رياضيات 1 + إنجليزي 1 + كيمياء 0 = 2 من 27
        assertEquals(2, progress.overall.completedLessons)
        assertEquals(27, progress.overall.totalLessons)
        assertEquals(7, progress.overall.percent)
    }

    @Test
    fun `finished subject does not advance further`() {
        val total = subjects.first { it.id == "chemistry" }.totalLessons
        var state = state().withEvent(StudentEvent.CurrentLessonSet("chemistry", total))
        var progress = calc.calculate(subjects, state.events)
        assertTrue(progress.subjectProgress.getValue("chemistry").isFinished)
        assertEquals(100, progress.subjectProgress.getValue("chemistry").percent)

        state = state.withDayAttendance("2026-09-16", listOf(SubjectAttendance("chemistry", AttendanceChoice.TAKEN)))
        progress = calc.calculate(subjects, state.events)
        assertEquals(total, progress.subjectProgress.getValue("chemistry").currentLessonIndex)
    }
}

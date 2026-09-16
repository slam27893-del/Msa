package com.msa.studyassistant.storage

import com.msa.studyassistant.model.AppState
import com.msa.studyassistant.model.AttendanceChoice
import com.msa.studyassistant.model.StudentEvent
import com.msa.studyassistant.model.StudentProfile
import com.msa.studyassistant.model.SubjectAttendance
import com.msa.studyassistant.model.WeekDay
import com.msa.studyassistant.model.WeeklySchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * اختبارات التخزين المحلي — بما فيها محاكاة "إغلاق التطبيق وإعادة فتحه"
 * (مستودع جديد يقرأ من نفس المخزن).
 */
class StudentStateRepositoryTest {

    @Test
    fun `fresh store starts with default state`() {
        val repo = StudentStateRepository(InMemoryKeyValueStore())
        assertEquals(AppState(), repo.state.value)
        assertNull(repo.state.value.profile)
    }

    @Test
    fun `state survives a simulated app restart`() {
        val store = InMemoryKeyValueStore()

        // الجلسة الأولى: إعداد + جدول + أحداث
        val first = StudentStateRepository(store)
        first.update { it.withProfile(StudentProfile()) }
        first.update { it.withSchedule(WeeklySchedule().withSubjectsForDay(WeekDay.SUNDAY, listOf("math"))) }
        first.update {
            it.withDayAttendance(
                "2026-09-13",
                listOf(
                    SubjectAttendance("math", AttendanceChoice.TAKEN),
                    SubjectAttendance("english", AttendanceChoice.ABSENT),
                ),
            )
        }
        first.update { it.withEvent(StudentEvent.LessonsTaken("math", listOf(1, 2))) }
        first.update { it.withEvent(StudentEvent.CurrentLessonSet("chemistry", 4)) }

        // "إغلاق التطبيق وفتحه من جديد": مستودع جديد من نفس المخزن
        val second = StudentStateRepository(store)
        assertEquals(first.state.value, second.state.value)
        assertNotNull(second.state.value.profile)
        assertEquals(listOf("math"), second.state.value.schedule.subjectsOn(WeekDay.SUNDAY))
        assertEquals(3, second.state.value.events.size)
        assertEquals(
            AttendanceChoice.ABSENT,
            second.state.value.dayAttendance("2026-09-13")?.entries?.firstOrNull { it.subjectId == "english" }?.choice,
        )
    }

    @Test
    fun `corrupted stored data falls back to default instead of crashing`() {
        val store = InMemoryKeyValueStore()
        store.putString(StudentStateRepository.KEY_APP_STATE, "ليست JSON صالحة }}}")
        val repo = StudentStateRepository(store)
        assertEquals(AppState(), repo.state.value)
    }

    @Test
    fun `updating the same day replaces its record`() {
        val repo = StudentStateRepository(InMemoryKeyValueStore())
        repo.update {
            it.withDayAttendance("2026-09-13", listOf(SubjectAttendance("math", AttendanceChoice.TAKEN)))
        }
        repo.update {
            it.withDayAttendance("2026-09-13", listOf(SubjectAttendance("math", AttendanceChoice.NOT_TAKEN)))
        }
        val events = repo.state.value.events.filterIsInstance<StudentEvent.DayAttendance>()
        assertEquals(1, events.size)
        assertEquals(AttendanceChoice.NOT_TAKEN, events.first().entries.first().choice)
    }
}

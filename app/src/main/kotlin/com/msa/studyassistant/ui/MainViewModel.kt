package com.msa.studyassistant.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.studyassistant.curriculum.CurriculumSubject
import com.msa.studyassistant.domain.AttendanceDefaults
import com.msa.studyassistant.domain.StudentProgress
import com.msa.studyassistant.domain.StudentProgressCalculator
import com.msa.studyassistant.domain.SubjectProgress
import com.msa.studyassistant.model.AppState
import com.msa.studyassistant.model.AttendanceChoice
import com.msa.studyassistant.model.CurriculumLesson
import com.msa.studyassistant.model.StudentEvent
import com.msa.studyassistant.model.StudentProfile
import com.msa.studyassistant.model.SubjectAttendance
import com.msa.studyassistant.model.WeeklySchedule
import com.msa.studyassistant.model.weekDay
import com.msa.studyassistant.storage.StudentStateRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel مركزي: يعرض الحالة والتقدم المشتق، ويحوّل أفعال الواجهة
 * إلى أحداث تُسجَّل في المستودع (وتُحفَظ محليًا تلقائيًا).
 */
class MainViewModel(
    private val repository: StudentStateRepository,
    val subjects: List<CurriculumSubject>,
) : ViewModel() {

    private val calculator = StudentProgressCalculator()

    val state: StateFlow<AppState> = repository.state

    /** التprogress المشتق من كل الأحداث — يتحدث تلقائيًا مع كل تعديل. */
    val progress: StateFlow<StudentProgress> = repository.state
        .map { calculator.calculate(subjects, it.events) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = calculator.calculate(subjects, repository.state.value.events),
        )

    // ------------------------------------------------------------------
    // استعلامات (تُستخدم من الشاشات)
    // ------------------------------------------------------------------

    fun subjectById(id: String?): CurriculumSubject? = subjects.firstOrNull { it.id == id }

    fun progressOf(subjectId: String): SubjectProgress =
        progress.value.subjectProgress[subjectId]
            ?: SubjectProgress(subjectId, 0, subjectById(subjectId)?.totalLessons ?: 0)

    fun currentLessonOf(subjectId: String): CurriculumLesson? =
        subjectById(subjectId)?.lessonAt(progressOf(subjectId).currentLessonIndex)

    fun subjectsFor(schedule: WeeklySchedule, date: LocalDate): List<CurriculumSubject> {
        val ids = schedule.subjectsOn(date.weekDay()).toSet()
        return subjects.filter { it.id in ids }
    }

    fun savedChoicesFor(date: LocalDate): Map<String, AttendanceChoice> =
        state.value.dayAttendance(date.toString())
            ?.entries
            ?.associate { it.subjectId to it.choice }
            ?: emptyMap()

    fun initialChoicesFor(
        scheduledSubjects: List<CurriculumSubject>,
        savedChoices: Map<String, AttendanceChoice>,
    ): Map<String, AttendanceChoice> =
        AttendanceDefaults.initialChoices(scheduledSubjects.map { it.id }, savedChoices)

    fun isSignificantJump(subjectId: String, targetIndex: Int): Boolean =
        calculator.isSignificantJump(progressOf(subjectId).currentLessonIndex, targetIndex)

    fun jumpDistance(subjectId: String, targetIndex: Int): Int =
        calculator.jumpDistance(progressOf(subjectId).currentLessonIndex, targetIndex)

    // ------------------------------------------------------------------
    // أفعال (من الواجهة)
    // ------------------------------------------------------------------

    fun saveProfile(profile: StudentProfile) = repository.update { it.withProfile(profile) }

    fun saveSchedule(schedule: WeeklySchedule) = repository.update { it.withSchedule(schedule) }

    /** تسجيل/تحديث يوم دراسي (زر «حضرت اليوم» أو تعديل حالات المواد). */
    fun recordDay(date: LocalDate, choices: Map<String, AttendanceChoice>) =
        repository.update {
            it.withDayAttendance(date.toString(), choices.map { (subjectId, choice) -> SubjectAttendance(subjectId, choice) })
        }

    /** تعليم درس أو أكثر بأنها أُخذت. */
    fun markLessonsTaken(subjectId: String, lessonIndices: Collection<Int>) {
        if (lessonIndices.isEmpty()) return
        repository.update {
            it.withEvent(StudentEvent.LessonsTaken(subjectId, lessonIndices.distinct().sorted()))
        }
    }

    /** تعيين درس كدرس حالي (الاختيار اليدوي). */
    fun setCurrentLesson(subjectId: String, lessonIndex: Int) =
        repository.update { it.withEvent(StudentEvent.CurrentLessonSet(subjectId, lessonIndex)) }
}

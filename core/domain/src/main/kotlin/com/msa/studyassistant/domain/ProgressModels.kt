package com.msa.studyassistant.domain

/**
 * نماذج التقدم المشتقة من الأحداث (لا تُخزَّن، بل تُحسب).
 */

/** تقدم الطالب في مادة واحدة. */
data class SubjectProgress(
    val subjectId: String,
    /** رقم الدرس الحالي = أول درس لم يُسجَّل أخذه (يبدأ من 0). */
    val currentLessonIndex: Int,
    val totalLessons: Int,
    /** دروس فائتة بسبب الغياب ولم تُسجَّل أُخذت لاحقًا. */
    val missedLessonIndices: Set<Int> = emptySet(),
) {
    /** الدروس المكتملة = كل الدروس قبل الدرس الحالي. */
    val completedLessons: Int get() = currentLessonIndex

    val percent: Int get() = if (totalLessons == 0) 0 else completedLessons * 100 / totalLessons

    val isFinished: Boolean get() = totalLessons > 0 && currentLessonIndex >= totalLessons

    /** رقم الدرس التالي (بعد الحالي) أو null إذا كان الحالي آخر درس. */
    val nextLessonIndex: Int? get() = if (isFinished || currentLessonIndex + 1 >= totalLessons) null else currentLessonIndex + 1
}

/** التقدم الكلي للفصل عبر جميع المواد. */
data class OverallProgress(
    val completedLessons: Int,
    val totalLessons: Int,
) {
    val percent: Int get() = if (totalLessons == 0) 0 else completedLessons * 100 / totalLessons
}

/** درس فائت: غاب عنه الطالب ولم يُسجَّل أنه أُخذ لاحقًا (حتى الآن). */
data class MissedLesson(
    val subjectId: String,
    val lessonIndex: Int,
    /** التواريخ التي تغيب فيها الطالب عن هذا الدرس. */
    val missedDates: List<String>,
)

/** التقدم الكلي المشتق من كل الأحداث. */
data class StudentProgress(
    val subjectProgress: Map<String, SubjectProgress> = emptyMap(),
    val missedLessons: List<MissedLesson> = emptyList(),
) {
    val overall: OverallProgress
        get() {
            val all = subjectProgress.values
            return OverallProgress(
                completedLessons = all.sumOf { it.completedLessons },
                totalLessons = all.sumOf { it.totalLessons },
            )
        }
}

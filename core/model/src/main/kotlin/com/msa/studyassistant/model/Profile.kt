package com.msa.studyassistant.model

import kotlinx.serialization.Serializable

/**
 * بيانات الطالب الأساسية (شاشة الإعداد).
 *
 * المرحلة والصف والفصل محددة حاليًا بخيار واحد (Prototype: ثانوي / أول ثانوي / الفصل الأول)
 * لكنها مبنية كـ Enums مع أسماء عربية حتى يسهل لاحقًا إضافة بقية الصفوف والفصول
 * دون تغيير أي منطق أو واجهة.
 */

enum class Stage(val displayName: String) {
    SECONDARY("ثانوي"),
}

enum class GradeLevel(val displayName: String) {
    FIRST_SECONDARY("أول ثانوي"),
    // لاحقًا: SECOND_SECONDARY("ثاني ثانوي"), THIRD_SECONDARY("ثالث ثانوي")
}

enum class Semester(val displayName: String) {
    FIRST("الفصل الأول"),
    // لاحقًا: SECOND("الفصل الثاني")
}

/** المسار: قيمة تجريبية حاليًا، تُستبدل عند إضافة المسارات الحقيقية. */
enum class StudyTrack(val displayName: String) {
    GENERAL("عام (تجريبي)"),
}

@Serializable
data class StudentProfile(
    val stage: Stage = Stage.SECONDARY,
    val grade: GradeLevel = GradeLevel.FIRST_SECONDARY,
    val semester: Semester = Semester.FIRST,
    val track: StudyTrack = StudyTrack.GENERAL,
)

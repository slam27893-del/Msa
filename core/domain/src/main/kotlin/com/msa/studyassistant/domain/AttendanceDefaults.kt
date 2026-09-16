package com.msa.studyassistant.domain

import com.msa.studyassistant.model.AttendanceChoice
import java.time.LocalDate

/**
 * منطق زر "حضرت اليوم":
 * إذا ضغط المستخدم الزر دون تعديل أي مادة، تُعتبر جميع المواد المجدولة
 * لذلك اليوم "تم أخذ الدرس"، فينتقل كل مادة لدرسها التالي.
 */
object AttendanceDefaults {

    /** الخيارات الافتراضية لمواد يوم معين (كلها "تم أخذ الدرس"). */
    fun defaultChoices(scheduledSubjectIds: List<String>): Map<String, AttendanceChoice> =
        scheduledSubjectIds.associateWith { AttendanceChoice.TAKEN }

    /**
     * الخيارات المبدئية لواجهة تسجيل اليوم:
     * - إذا كان اليوم مسجلًا سابقًا نبدأ بقيمه المحفوظة (لإتاحة التعديل).
     * - وإلا فالافتراضي "تم أخذ الدرس" لكل مادة مجدولة.
     */
    fun initialChoices(
        scheduledSubjectIds: List<String>,
        savedChoices: Map<String, AttendanceChoice>,
    ): Map<String, AttendanceChoice> =
        scheduledSubjectIds.associateWith { savedChoices[it] ?: AttendanceChoice.TAKEN }
}

/** تحويل تاريخ إلى مفتاح ISO-8601 (مثل 2026-09-16) لاستخدامه في السجلات. */
fun LocalDate.toIsoDate(): String = toString()

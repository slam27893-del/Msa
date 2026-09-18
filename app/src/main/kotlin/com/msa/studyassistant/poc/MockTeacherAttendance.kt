// PROOF OF CONCEPT - MOCK DATA
// محاكاة غياب المادة من نظام حضور المعلم — تجريبي بالكامل.
// لا يوجد أي ربط بنظام رصد حضور المدرسة؛ الهدف عرض الفكرة في المقترح فقط.
// يعيد استخدام نفس منطق الغياب الموجود في التطبيق (core/domain) دون أي تعديل عليه:
// الغياب لا يقدم الدرس الحالي، والدرس يظهر تلقائيًا في تبويب «الدروس الفائتة».

package com.msa.studyassistant.poc

import com.msa.studyassistant.model.AttendanceChoice
import com.msa.studyassistant.ui.MainViewModel
import java.time.LocalDate

object MockTeacherAttendance {

    /**
     * تسجيل غياب تجريبي لمادة في تاريخ معين (افتراضيًا اليوم).
     * يحافظ على تسجيلات اليوم الأخرى إن وُجدت، ويضيف غياب المادة المختارة فقط.
     */
    fun recordAbsence(
        viewModel: MainViewModel,
        subjectId: String,
        date: LocalDate = LocalDate.now(),
    ) {
        val savedChoices = viewModel.savedChoicesFor(date)
        viewModel.recordDay(date, savedChoices + (subjectId to AttendanceChoice.ABSENT))
    }
}

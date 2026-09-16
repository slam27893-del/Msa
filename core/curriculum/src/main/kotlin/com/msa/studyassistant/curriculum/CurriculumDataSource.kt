package com.msa.studyassistant.curriculum

import com.msa.studyassistant.model.CurriculumSubject
import com.msa.studyassistant.model.StudentProfile

/**
 * مصدر بيانات المنهج — الطبقة التي تفصل "بيانات المنهج" عن بقية التطبيق.
 *
 * الآن: بيانات تجريبية لثلاث مواد ([SampleCurriculum]).
 * لاحقًا: يُستبدل بتنفيذ يقرأ المناهج السعودية الحقيقية
 * (ملفات، قاعدة بيانات محلية، أو مصدر سحابي) دون تغيير الواجهة أو منطق التقدم.
 */
interface CurriculumDataSource {
    fun subjectsFor(profile: StudentProfile): List<CurriculumSubject>
}

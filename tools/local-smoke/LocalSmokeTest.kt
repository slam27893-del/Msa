import com.msa.studyassistant.curriculum.SampleCurriculum
import com.msa.studyassistant.domain.AttendanceDefaults
import com.msa.studyassistant.domain.StudentProgressCalculator
import com.msa.studyassistant.model.AppState
import com.msa.studyassistant.model.AttendanceChoice
import com.msa.studyassistant.model.StudentEvent
import com.msa.studyassistant.model.StudentProfile
import com.msa.studyassistant.model.SubjectAttendance
import com.msa.studyassistant.model.WeekDay
import com.msa.studyassistant.model.WeeklySchedule
import com.msa.studyassistant.model.weekDay
import java.time.LocalDate

/**
 * اختبار دخان محلي لمنطق التقدم — يغطي القواعد الأساسية للـ Prototype.
 * يعمل بـ kotlinc + java فقط (بدون Gradle أو Android SDK).
 * النسخة الرسمية من الاختبارات موجودة داخل الوحدات وتُشغَّل عبر Gradle في CI.
 */

private var passedCount = 0

private fun check(name: String, condition: Boolean) {
    if (!condition) throw AssertionError("فشل الفحص: $name")
    passedCount++
    println("  [نجاح] $name")
}

private fun section(name: String) = println("\n== $name ==")

fun main() {
    val subjects = SampleCurriculum.subjectsFor(StudentProfile())
    val calc = StudentProgressCalculator()

    section("سلامة بيانات المنهج التجريبية")
    check("ثلاث مواد", subjects.size == 3)
    check("كل مادة بها وحدات ودروس مرتبة", subjects.all { it.units.isNotEmpty() && it.lessons.isNotEmpty() })
    check("معرفات الدروس فريدة", subjects.flatMap { s -> s.lessons.map { it.id } }.distinct().size == subjects.sumOf { it.totalLessons })
    check("الإجمالي 27 درسًا (3 مواد × 9)", subjects.sumOf { it.totalLessons } == 27)
    check("الوحدة للدرس الأخير صحيحة", subjects.first { it.id == "math" }.unitForGlobalIndex(8)?.id == "math-u3")

    section("تعيين أيام الأسبوع")
    check("2026-09-13 هو الأحد", LocalDate.of(2026, 9, 13).weekDay() == WeekDay.SUNDAY)
    check("2026-09-16 هو الأربعاء", LocalDate.of(2026, 9, 16).weekDay() == WeekDay.WEDNESDAY)

    section("القاعدة 1: «تم أخذ الدرس» ينقل للدرس التالي")
    var state = AppState(
        schedule = WeeklySchedule().withSubjectsForDay(WeekDay.SUNDAY, listOf("math", "english")),
    )
    state = state.withDayAttendance("2026-09-13", listOf(SubjectAttendance("math", AttendanceChoice.TAKEN)))
    var p = calc.calculate(subjects, state.events)
    check("الرياضيات انتقلت للدرس الثاني", p.subjectProgress.getValue("math").currentLessonIndex == 1)
    check("الإنجليزي لم يتأثر (ليس في هذا التسجيل)", p.subjectProgress.getValue("english").currentLessonIndex == 0)

    section("القاعدة 2: «لم نأخذ الدرس» لا يغير الدرس الحالي")
    state = state.withDayAttendance(
        "2026-09-13",
        listOf(
            SubjectAttendance("math", AttendanceChoice.TAKEN),
            SubjectAttendance("english", AttendanceChoice.NOT_TAKEN),
        ),
    )
    p = calc.calculate(subjects, state.events)
    check("الرياضيات = 1", p.subjectProgress.getValue("math").currentLessonIndex == 1)
    check("الإنجليزي = 0", p.subjectProgress.getValue("english").currentLessonIndex == 0)
    check("لا دروس فائتة", p.missedLessons.isEmpty())

    section("القاعدة 3: «غياب» لا يحرك الدرس ويسجل الدرس فائتًا")
    state = state.withDayAttendance(
        "2026-09-13",
        listOf(
            SubjectAttendance("math", AttendanceChoice.TAKEN),
            SubjectAttendance("english", AttendanceChoice.ABSENT),
        ),
    )
    p = calc.calculate(subjects, state.events)
    check("الإنجليزي ما زال = 0 (الغياب ليس أخذًا)", p.subjectProgress.getValue("english").currentLessonIndex == 0)
    check("الدرس الأول من الإنجليزي فائت", p.missedLessons.size == 1 && p.missedLessons[0].subjectId == "english" && p.missedLessons[0].lessonIndex == 0)
    check("تاريخ الغياب مسجل", p.missedLessons[0].missedDates == listOf("2026-09-13"))

    section("زر «حضرت اليوم» (دون تعديل: كل المواد مأخوذة)")
    val defaults = AttendanceDefaults.defaultChoices(listOf("math", "english"))
    check("الافتراضي كله «تم أخذ الدرس»", defaults.values.all { it == AttendanceChoice.TAKEN })
    state = state.withDayAttendance("2026-09-13", defaults.map { SubjectAttendance(it.key, it.value) })
    p = calc.calculate(subjects, state.events)
    check("الرياضيات = 1", p.subjectProgress.getValue("math").currentLessonIndex == 1)
    check("الإنجليزي = 1", p.subjectProgress.getValue("english").currentLessonIndex == 1)
    check("الفائتة انحلت بعد الأخذ", p.missedLessons.isEmpty())

    section("تعديل تسجيل اليوم نفسه يعيد الحساب بشكل صحيح")
    state = state.withDayAttendance(
        "2026-09-13",
        listOf(
            SubjectAttendance("math", AttendanceChoice.TAKEN),
            SubjectAttendance("english", AttendanceChoice.ABSENT),
        ),
    )
    p = calc.calculate(subjects, state.events)
    check("الإنجليزي رجع = 0", p.subjectProgress.getValue("english").currentLessonIndex == 0)
    check("عاد للفائتة", p.missedLessons.map { it.subjectId } == listOf("english"))

    section("غياب متكرر = تواريخ متعددة لنفس الدرس")
    state = state.withDayAttendance("2026-09-20", listOf(SubjectAttendance("english", AttendanceChoice.ABSENT)))
    p = calc.calculate(subjects, state.events)
    check("درس واحد فائت بتاريخين", p.missedLessons.size == 1 && p.missedLessons[0].missedDates == listOf("2026-09-13", "2026-09-20"))

    section("أخذ أكثر من درس دفعة واحدة")
    state = state.withEvent(StudentEvent.LessonsTaken("math", listOf(1, 2, 3)))
    p = calc.calculate(subjects, state.events)
    check("الرياضيات = 4", p.subjectProgress.getValue("math").currentLessonIndex == 4)
    check("التقدم 4 من 9 (44%)", p.subjectProgress.getValue("math").completedLessons == 4 && p.subjectProgress.getValue("math").percent == 44)

    section("تعليم الدرس الفائت كمأخوذ لاحقًا يحله")
    state = state.withEvent(StudentEvent.LessonsTaken("english", listOf(0)))
    p = calc.calculate(subjects, state.events)
    check("الإنجليزي = 1", p.subjectProgress.getValue("english").currentLessonIndex == 1)
    check("قائمة الفائتة فارغة", p.missedLessons.isEmpty())

    section("اختيار درس يدويًا (أمامًا وخلفًا)")
    state = state.withEvent(StudentEvent.CurrentLessonSet("math", 7))
    p = calc.calculate(subjects, state.events)
    check("قفز للأمام إلى 7", p.subjectProgress.getValue("math").currentLessonIndex == 7)
    state = state.withEvent(StudentEvent.CurrentLessonSet("math", 2))
    p = calc.calculate(subjects, state.events)
    check("رجوع للخلف إلى 2", p.subjectProgress.getValue("math").currentLessonIndex == 2)
    check("القفز الكبير يُكتشف (يحتاج تأكيدًا)", calc.isSignificantJump(2, 7))
    check("القفز الصغير لا يحتاج تأكيدًا", !calc.isSignificantJump(2, 4))

    section("التقدم الكلي للفصل")
    check("الإجمالي 3 من 27", p.overall.completedLessons == 3 && p.overall.totalLessons == 27)
    check("النسبة 11%", p.overall.percent == 11)
    check("الرياضيات 2/9 (22%)", p.subjectProgress.getValue("math").completedLessons == 2 && p.subjectProgress.getValue("math").percent == 22)

    section("مادة منتهية لا تتقدم أكثر")
    state = state.withEvent(StudentEvent.CurrentLessonSet("chemistry", 9))
    state = state.withEvent(StudentEvent.LessonsTaken("chemistry", listOf(0, 8)))
    p = calc.calculate(subjects, state.events)
    check("الكيمياء منتهية 100%", p.subjectProgress.getValue("chemistry").isFinished && p.subjectProgress.getValue("chemistry").percent == 100)
    val before = p.subjectProgress.getValue("chemistry").currentLessonIndex
    state = state.withDayAttendance("2026-09-16", listOf(SubjectAttendance("chemistry", AttendanceChoice.TAKEN)))
    p = calc.calculate(subjects, state.events)
    check("لا تجاوز بعد نهاية المنهج", p.subjectProgress.getValue("chemistry").currentLessonIndex == before)

    println()
    println("===== نجحت جميع الفحوصات ($passedCount فحصًا) =====")
}

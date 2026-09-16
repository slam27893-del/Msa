package com.msa.studyassistant.curriculum

import com.msa.studyassistant.model.CurriculumLesson
import com.msa.studyassistant.model.CurriculumSubject
import com.msa.studyassistant.model.CurriculumUnit
import com.msa.studyassistant.model.StudentProfile

/**
 * بيانات منهج تجريبية لثلاث مواد (أول ثانوي — الفصل الأول).
 *
 * مهم: هذه بيانات وهمية لأغراض التجربة فقط، وليست المنهج السعودي الرسمي.
 * استبدالها لاحقًا لا يتطلب أي تعديل خارج هذه الطبقة.
 */
object SampleCurriculum : CurriculumDataSource {

    override fun subjectsFor(profile: StudentProfile): List<CurriculumSubject> = subjects

    // --------------------------------------------------------------------
    // الرياضيات
    // --------------------------------------------------------------------
    private val math = CurriculumSubject(
        id = "math",
        name = "الرياضيات",
        units = listOf(
            CurriculumUnit(
                id = "math-u1",
                title = "الوحدة الأولى: الأسس والجذور",
                lessons = listOf(
                    CurriculumLesson(
                        id = "math-u1-l1",
                        title = "قوانين الأسس الصحيحة",
                        objectives = listOf(
                            "أن يطبق الطالب قوانين ضرب وقسمة القوى ذات الأساس الواحد.",
                            "أن يبسّط مقادير جبرية تحتوي على أسس.",
                        ),
                        keyConcepts = listOf("القاعدة", "الأس", "الأس الصفري"),
                        summary = "يتناول الدرس قوانين التعامل مع الأسس عند الضرب والقسمة ورفع القوة إلى قوة، مع أمثلة تطبيقية.",
                    ),
                    CurriculumLesson(
                        id = "math-u1-l2",
                        title = "الأسس النسبية",
                        objectives = listOf(
                            "أن يعبّر الطالب عن الجذور باستخدام الأسس النسبية.",
                            "أن يحسب قيمًا عددية تحتوي أسسًا نسبية.",
                        ),
                        keyConcepts = listOf("الأس النسبي", "الجذر التربيعي", "الجذر التكعيبي"),
                        summary = "يربط الدرس بين صورة الجذر وصورة الأس النسبي ويدرب على التحويل بينهما.",
                    ),
                    CurriculumLesson(
                        id = "math-u1-l3",
                        title = "الجذور والتقدير التقريبي",
                        objectives = listOf(
                            "أن يقدّر الطالب قيم الجذور تقديرًا تقريبيًا.",
                            "أن يقارن بين مقادير تتضمن جذورًا.",
                        ),
                        keyConcepts = listOf("التقدير التقريبي", "المربعات الكاملة"),
                        summary = "يتدرب الطالب على تقدير جذور الأعداد غير المربعة الكاملة وترتيبها.",
                    ),
                ),
            ),
            CurriculumUnit(
                id = "math-u2",
                title = "الوحدة الثانية: المعادلات والمتباينات",
                lessons = listOf(
                    CurriculumLesson(
                        id = "math-u2-l1",
                        title = "المعادلات الخطية",
                        objectives = listOf(
                            "أن يحل الطالب معادلات خطية بمتغير واحد.",
                            "أن يتحقق من صحة الحل بالتعويض.",
                        ),
                        keyConcepts = listOf("المعادلة", "الحل", "التعويض"),
                        summary = "مراجعة موسعة لحل المعادلات الخطية وخطوات نقل الحدود وعزل المتغير.",
                    ),
                    CurriculumLesson(
                        id = "math-u2-l2",
                        title = "المتباينات الخطية",
                        objectives = listOf(
                            "أن يحل الطالب متباينات خطية ويمثل حلولها على خط الأعداد.",
                        ),
                        keyConcepts = listOf("المتباينة", "عكس الإشارة", "خط الأعداد"),
                        summary = "حل المتباينات الخطية مع الانتباه لعكس اتجاه المتباينة عند الضرب أو القسمة على عدد سالب.",
                    ),
                    CurriculumLesson(
                        id = "math-u2-l3",
                        title = "تطبيقات على المعادلات",
                        objectives = listOf(
                            "أن يترجم الطالب مسألة لفظية إلى معادلة ويحلها.",
                        ),
                        keyConcepts = listOf("المسائل اللفظية", "النمذجة الجبرية"),
                        summary = "مسائل حياتية تُترجم إلى معادلات خطية مثل المسافات والأسعار والنسب.",
                    ),
                ),
            ),
            CurriculumUnit(
                id = "math-u3",
                title = "الوحدة الثالثة: الدوال",
                lessons = listOf(
                    CurriculumLesson(
                        id = "math-u3-l1",
                        title = "مفهوم الدالة",
                        objectives = listOf(
                            "أن يميز الطالب الدالة من غير الدالة.",
                            "أن يحدد مجال الدالة ومداها.",
                        ),
                        keyConcepts = listOf("الدالة", "المجال", "المدى"),
                        summary = "تعريف الدالة كعلاقة تربط كل مدخل بمخرج واحد، مع أمثلة وتمثيلات مختلفة.",
                    ),
                    CurriculumLesson(
                        id = "math-u3-l2",
                        title = "الدالة الخطية وتمثيلها",
                        objectives = listOf(
                            "أن يمثل الطالب الدالة الخطية بيانياً.",
                            "أن يحدد الميل والتقاطع من المعادلة أو الرسم.",
                        ),
                        keyConcepts = listOf("الميل", "التقاطع الصادي", "التمثيل البياني"),
                        summary = "دراسة صورة الدالة الخطية y = mx + b وعلاقة الميل والتقاطع بشكل الخط المستقيم.",
                    ),
                    CurriculumLesson(
                        id = "math-u3-l3",
                        title = "النسبة والتناسب",
                        objectives = listOf(
                            "أن يحل الطالب مسائل النسبة والتناسب.",
                            "أن يستخدم التناسب الطردي في المسائل الحياتية.",
                        ),
                        keyConcepts = listOf("النسبة", "التناسب", "التناسب الطردي"),
                        summary = "تطبيقات على النسب والتناسب في السياقات اليومية مثل الوصفات والمقاييس.",
                    ),
                ),
            ),
        ),
    )

    // --------------------------------------------------------------------
    // اللغة الإنجليزية
    // --------------------------------------------------------------------
    private val english = CurriculumSubject(
        id = "english",
        name = "اللغة الإنجليزية",
        units = listOf(
            CurriculumUnit(
                id = "english-u1",
                title = "Unit 1: Back to School",
                lessons = listOf(
                    CurriculumLesson(
                        id = "english-u1-l1",
                        title = "Vocabulary: School Life",
                        objectives = listOf(
                            "أن يستخدم الطالب المفردات الجديدة في جمل مفيدة.",
                            "أن يربط الكلمات بمعانيها في سياق المدرسة.",
                        ),
                        keyConcepts = listOf("Subjects", "Classroom objects", "Adjectives"),
                        summary = "مفردات أساسية عن الحياة المدرسية مع أنشطة مطابقة وتعبئة فراغات.",
                    ),
                    CurriculumLesson(
                        id = "english-u1-l2",
                        title = "Grammar: Present Simple",
                        objectives = listOf(
                            "أن يصوغ الطالب جملًا في المضارع البسيط إثباتًا ونفيًا وسؤالًا.",
                        ),
                        keyConcepts = listOf("Present Simple", "Third person -s", "Do / Does"),
                        summary = "قواعد المضارع البسيط واستخدامه للعادات والحقائق الثابتة.",
                    ),
                    CurriculumLesson(
                        id = "english-u1-l3",
                        title = "Reading: A New School Year",
                        objectives = listOf(
                            "أن يجيب الطالب عن أسئلة فهم مقروء.",
                            "أن يستنتج المعنى من السياق.",
                        ),
                        keyConcepts = listOf("Skimming", "Scanning", "Context clues"),
                        summary = "نص قصصي عن بداية عام دراسي جديد مع أسئلة استيعاب واستنتاج.",
                    ),
                ),
            ),
            CurriculumUnit(
                id = "english-u2",
                title = "Unit 2: Daily Routines",
                lessons = listOf(
                    CurriculumLesson(
                        id = "english-u2-l1",
                        title = "Vocabulary: Daily Activities",
                        objectives = listOf(
                            "أن يصف الطالب يومه بالمفردات المناسبة.",
                        ),
                        keyConcepts = listOf("Time expressions", "Routine verbs"),
                        summary = "أفعال وتعبيرات زمنية لوصف الروتين اليومي.",
                    ),
                    CurriculumLesson(
                        id = "english-u2-l2",
                        title = "Grammar: Adverbs of Frequency",
                        objectives = listOf(
                            "أن يوظف الطالب ظروف التكرار في الجمل صحيحًا.",
                        ),
                        keyConcepts = listOf("Always", "Usually", "Sometimes", "Never"),
                        summary = "موقع ظروف التكرار في الجملة مع المضارع البسيط.",
                    ),
                    CurriculumLesson(
                        id = "english-u2-l3",
                        title = "Speaking: My Routine",
                        objectives = listOf(
                            "أن يتحدث الطالب عن روتينه اليومي بجمل مترابطة.",
                        ),
                        keyConcepts = listOf("Linking words", "Pronunciation"),
                        summary = "نشاط حواري ثنائي لوصف اليوم الدراسي والمنزلي.",
                    ),
                ),
            ),
            CurriculumUnit(
                id = "english-u3",
                title = "Unit 3: Our World",
                lessons = listOf(
                    CurriculumLesson(
                        id = "english-u3-l1",
                        title = "Vocabulary: Nature and Weather",
                        objectives = listOf(
                            "أن يسمي الطالب ظواهر طبيعية وحالات الطقس بالإنجليزية.",
                        ),
                        keyConcepts = listOf("Weather words", "Landforms"),
                        summary = "مفردات الطقس والطبيعة مع وصف صور وخرائط.",
                    ),
                    CurriculumLesson(
                        id = "english-u3-l2",
                        title = "Grammar: Comparatives",
                        objectives = listOf(
                            "أن يقارن الطالب بين شيئين باستخدام صفات المقارنة.",
                        ),
                        keyConcepts = listOf("-er / more", "Than", "Irregular comparatives"),
                        summary = "تكوين صفات المقارنة واستخدامها في الوصف والمقارنة.",
                    ),
                    CurriculumLesson(
                        id = "english-u3-l3",
                        title = "Writing: Describing a Place",
                        objectives = listOf(
                            "أن يكتب الطالب فقرة وصفية عن مكان يحبه.",
                        ),
                        keyConcepts = listOf("Topic sentence", "Supporting details"),
                        summary = "خطوات كتابة الفقرة الوصفية من الجملة الرئيسية إلى التفاصيل.",
                    ),
                ),
            ),
        ),
    )

    // --------------------------------------------------------------------
    // الكيمياء
    // --------------------------------------------------------------------
    private val chemistry = CurriculumSubject(
        id = "chemistry",
        name = "الكيمياء",
        units = listOf(
            CurriculumUnit(
                id = "chemistry-u1",
                title = "الوحدة الأولى: المادة وتغيراتها",
                lessons = listOf(
                    CurriculumLesson(
                        id = "chemistry-u1-l1",
                        title = "حالات المادة",
                        objectives = listOf(
                            "أن يقارن الطالب بين حالات المادة الثلاث من حيث الشكل والحجم.",
                            "أن يفسر انتقال المادة بين الحالات.",
                        ),
                        keyConcepts = listOf("الصلبة", "السائلة", "الغازية", "الانصهار", "التبخر"),
                        summary = "خصائص حالات المادة الثلاث وتحولاتها بالتسخين والتبريد.",
                    ),
                    CurriculumLesson(
                        id = "chemistry-u1-l2",
                        title = "التغيرات الفيزيائية والكيميائية",
                        objectives = listOf(
                            "أن يميز الطالب بين التغير الفيزيائي والكيميائي بأمثلة.",
                        ),
                        keyConcepts = listOf("تغير فيزيائي", "تغير كيميائي", "مادة جديدة"),
                        summary = "الفرق بين التغير الذي يحفظ نوع المادة والتغير الذي ينتج مادة جديدة.",
                    ),
                    CurriculumLesson(
                        id = "chemistry-u1-l3",
                        title = "المخاليط والمركبات",
                        objectives = listOf(
                            "أن يصنف الطالب الأمثلة إلى مخاليط ومركبات.",
                            "أن يقترح طريقة لفصل مخلوط بسيط.",
                        ),
                        keyConcepts = listOf("مخلوط", "مركب", "الترشيح", "التقطير"),
                        summary = "أنواع المخاليط وطرق فصلها، والفرق بين المخلوط والمركب النقي.",
                    ),
                ),
            ),
            CurriculumUnit(
                id = "chemistry-u2",
                title = "الوحدة الثانية: الذرة",
                lessons = listOf(
                    CurriculumLesson(
                        id = "chemistry-u2-l1",
                        title = "تركيب الذرة",
                        objectives = listOf(
                            "أن يحدد الطالب مكونات الذرة ورموزها.",
                            "أن يحسب عدد البروتونات والنيوترونات والإلكترونات.",
                        ),
                        keyConcepts = listOf("البروتون", "النيوترون", "الإلكترون", "العدد الذري"),
                        summary = "مكونات الذرة وشحناتها وحساب مكونات ذرة عنصر معطى.",
                    ),
                    CurriculumLesson(
                        id = "chemistry-u2-l2",
                        title = "التوزيع الإلكتروني",
                        objectives = listOf(
                            "أن يكتب الطالب التوزيع الإلكتروني لعناصر الفترة الأولى والثانية.",
                        ),
                        keyConcepts = listOf("مستويات الطاقة", "القاعدة 2n²"),
                        summary = "توزيع إلكترونات الذرة على مستويات الطاقة حتى 18 إلكترونًا.",
                    ),
                    CurriculumLesson(
                        id = "chemistry-u2-l3",
                        title = "الجدول الدوري",
                        objectives = listOf(
                            "أن يقرأ الطالب معلومات العنصر من الجدول الدوري.",
                            "أن يميز بين المجموعة والدورة.",
                        ),
                        keyConcepts = listOf("المجموعة", "الدورة", "الرمز الذري"),
                        summary = "تنظيم العناصر في الجدول الدوري ودلالة موقع العنصر فيه.",
                    ),
                ),
            ),
            CurriculumUnit(
                id = "chemistry-u3",
                title = "الوحدة الثالثة: الروابط الكيميائية",
                lessons = listOf(
                    CurriculumLesson(
                        id = "chemistry-u3-l1",
                        title = "الرابطة الأيونية",
                        objectives = listOf(
                            "أن يفسر الطالب تكوّن الرابطة الأيونية بين فلز ولافلز.",
                        ),
                        keyConcepts = listOf("الفلز", "اللافلز", "الأيون الموجب والسالب"),
                        summary = "انتقال الإلكترونات وتكوين أيونات متجاذبة في المركبات الأيونية.",
                    ),
                    CurriculumLesson(
                        id = "chemistry-u3-l2",
                        title = "الرابطة التساهمية",
                        objectives = listOf(
                            "أن يمثل الطالب الرابطة التساهمية بوساطة الأشكال الإلكترونية.",
                        ),
                        keyConcepts = listOf("المشاركة", "الجزيء", "الرابطة الثنائية"),
                        summary = "مشاركة أزواج إلكترونية بين ذرتين ومثال جزيء الماء.",
                    ),
                    CurriculumLesson(
                        id = "chemistry-u3-l3",
                        title = "خصائص المركبات الأيونية والتساهمية",
                        objectives = listOf(
                            "أن يقارن الطالب بين خصائص المركبات الأيونية والتساهمية.",
                        ),
                        keyConcepts = listOf("درجة الانصهار", "التوصيل الكهربائي", "الذوبان"),
                        summary = "مقارنة تجريبية بين المركبين من حيث الحالة والانصهار والتوصيل.",
                    ),
                ),
            ),
        ),
    )

    val subjects: List<CurriculumSubject> = listOf(math, english, chemistry)
}

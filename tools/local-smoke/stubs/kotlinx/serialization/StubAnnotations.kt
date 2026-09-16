package kotlinx.serialization

/**
 * STUB محلي للاختبار بدون Gradle (أدوات/local-smoke) — غير مستخدم في بناء المشروع الحقيقي.
 * البناء الحقيقي يستخدم مكتبة kotlinx-serialization الفعلية.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Serializable

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SerialName(val name: String)

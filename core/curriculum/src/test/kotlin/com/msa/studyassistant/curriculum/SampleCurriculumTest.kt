package com.msa.studyassistant.curriculum

import com.msa.studyassistant.model.StudentProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * فحوصات سلامة بيانات المنهج التجريبية.
 */
class SampleCurriculumTest {

    private val subjects = SampleCurriculum.subjectsFor(StudentProfile())

    @Test
    fun `three sample subjects exist`() {
        assertEquals(3, subjects.size)
        assertEquals(listOf("math", "english", "chemistry"), subjects.map { it.id })
    }

    @Test
    fun `every subject has ordered units and lessons`() {
        subjects.forEach { subject ->
            assertTrue("${subject.name} يجب أن تحتوي وحدات", subject.units.isNotEmpty())
            subject.units.forEach { unit ->
                assertTrue("وحدة ${unit.title} يجب أن تحتوي دروسًا", unit.lessons.isNotEmpty())
            }
        }
    }

    @Test
    fun `lesson ids are unique across the curriculum`() {
        val allIds = subjects.flatMap { s -> s.lessons.map { it.id } }
        assertEquals(allIds.size, allIds.distinct().size)
    }

    @Test
    fun `lesson content is populated for the lesson screen`() {
        subjects.flatMap { it.lessons }.forEach { lesson ->
            assertTrue(lesson.title.isNotBlank())
            assertTrue("${lesson.id} أهداف فارغة", lesson.objectives.isNotEmpty())
            assertTrue("${lesson.id} مفاهيم فارغة", lesson.keyConcepts.isNotEmpty())
            assertTrue("${lesson.id} ملخص فارغ", lesson.summary.isNotBlank())
        }
    }

    @Test
    fun `global index maps back to the correct unit`() {
        val math = subjects.first { it.id == "math" }
        assertEquals("math-u1", math.unitForGlobalIndex(0)?.id)
        assertEquals("math-u2", math.unitForGlobalIndex(3)?.id)
        assertEquals("math-u3", math.unitForGlobalIndex(6)?.id)
        assertNotNull(math.lessonAt(8))
        assertEquals(null, math.lessonAt(9))
    }
}

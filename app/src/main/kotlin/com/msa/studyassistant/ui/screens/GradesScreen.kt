// PROOF OF CONCEPT - MOCK DATA
// شاشة الدرجات (محاكاة نظام نور): إدخال يدوي تجريبي + محاكاة حضور المعلم.
// لا يوجد أي ربط حقيقي بنور أو بنظام حضور المدرسة — عرض توضيحي للمقترح فقط.

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.msa.studyassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.studyassistant.data.GradesStore
import com.msa.studyassistant.domain.SubjectProgress
import com.msa.studyassistant.model.CurriculumSubject
import com.msa.studyassistant.poc.MockTeacherAttendance
import com.msa.studyassistant.ui.MainViewModel
import com.msa.studyassistant.ui.components.SubjectDot
import java.time.LocalDate
import kotlinx.coroutines.launch

/**
 * شاشة الدرجات (بيانات تجريبية):
 * 1) إدخال الدرجة يدويًا لكل مادة ومقارنتها بنسبة التقدم في المنهج.
 * 2) محاكاة «تسجيل حضور من المعلم» لتجربة فكرة الربط المستقبلي بنظام الحضور.
 */
@Composable
fun GradesScreen(
    viewModel: MainViewModel,
    gradesStore: GradesStore,
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val grades by gradesStore.grades.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var simSubjectId by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "title") {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "الدرجات",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "أدخل درجاتك يدويًا وقارنها بتقدمك في المنهج.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item(key = "noor-note") {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "الدرجات تُدخل يدويًا حاليًا — بيانات تجريبية تمهيدًا لربط رسمي مستقبلي مع نظام نور عند توفر التصريح.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }

            items(viewModel.subjects, key = { it.id }) { subject ->
                GradeSubjectCard(
                    subject = subject,
                    subjectProgress = progress.subjectProgress[subject.id],
                    grade = grades[subject.id],
                    onGradeChange = { gradesStore.setGrade(subject.id, it) },
                )
            }

            // ------------------------------------------------------------
            // محاكاة نظام حضور المعلم (تجريبي)
            // ------------------------------------------------------------
            item(key = "teacher-sim-title") {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "تسجيل حضور من المعلم (تجريبي)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "محاكاة لما لو تم ربط التطبيق مستقبلًا مع نظام رصد حضور المدرسة — الغياب يُسجَّل بنفس منطق التطبيق الحالي.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item(key = "teacher-sim-card") {
                OutlinedCard(shape = RoundedCornerShape(14.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "اختر المادة لتسجيل غياب تجريبي عنها اليوم:",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            viewModel.subjects.forEach { subject ->
                                FilterChip(
                                    selected = simSubjectId == subject.id,
                                    onClick = {
                                        simSubjectId = if (simSubjectId == subject.id) null else subject.id
                                    },
                                    label = { Text(subject.name) },
                                    leadingIcon = {
                                        if (simSubjectId == subject.id) {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer,
                                    ),
                                )
                            }
                        }
                        Button(
                            enabled = simSubjectId != null,
                            onClick = {
                                val subjectId = simSubjectId ?: return@Button
                                val subjectName = viewModel.subjectById(subjectId)?.name ?: subjectId
                                // PROOF OF CONCEPT - MOCK DATA
                                MockTeacherAttendance.recordAbsence(viewModel, subjectId, LocalDate.now())
                                simSubjectId = null
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "تم تسجيل غياب تجريبي عن «$subjectName» — راجع تبويب «الفائتة»",
                                        duration = SnackbarDuration.Long,
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(text = "تسجيل غياب (تجريبي)", style = MaterialTheme.typography.titleSmall)
                        }
                        Text(
                            text = "لن يتقدم الدرس الحالي، وسيظهر تلقائيًا ضمن الدروس الفائتة بتاريخ اليوم.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item(key = "footer-note") {
                Text(
                    text = "بيانات تجريبية توضيحية",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

/** بطاقة مادة في شاشة الدرجات: إدخال الدرجة + مقارنة بصرية مع تقدم المنهج. */
@Composable
private fun GradeSubjectCard(
    subject: CurriculumSubject,
    subjectProgress: SubjectProgress?,
    grade: Int?,
    onGradeChange: (Int?) -> Unit,
) {
    OutlinedCard(shape = RoundedCornerShape(14.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubjectDot(subject.id)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
            }

            val progressPercent = subjectProgress?.percent ?: 0
            ComparisonBarRow(label = "المنهج", percent = progressPercent, highlight = true)
            ComparisonBarRow(label = "الدرجة", percent = grade, highlight = false)

            Text(
                text = if (grade == null) {
                    "تقدمك بالمنهج $progressPercent% — أدخل درجتك لمقارنتها."
                } else {
                    "تقدمك بالمنهج $progressPercent% ودرجتك $grade%"
                },
                style = MaterialTheme.typography.bodyMedium,
            )

            gradeInsight(progressPercent, grade)?.let { insight ->
                InsightCard(text = insight, warning = progressPercent < 50 && (grade ?: 100) < 60)
            }

            GradeInputField(grade = grade, onGradeChange = onGradeChange)
        }
    }
}

/** صف مقارنة: تسمية + شريط + قيمة. */
@Composable
private fun ComparisonBarRow(label: String, percent: Int?, highlight: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(48.dp),
        )
        LinearProgressIndicator(
            progress = { (percent ?: 0) / 100f },
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (percent == null) "—" else "$percent%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.width(40.dp),
        )
    }
}

/** حقل إدخال الدرجة (0..100) — إدخال يدوي تجريبي. */
@Composable
private fun GradeInputField(grade: Int?, onGradeChange: (Int?) -> Unit) {
    var text by remember(grade) { mutableStateOf(grade?.toString().orEmpty()) }

    OutlinedTextField(
        value = text,
        onValueChange = { raw ->
            var digits = raw.filter { it.isDigit() }.take(3)
            val value = digits.toIntOrNull()
            if (value != null && value > 100) digits = "100"
            text = digits
            onGradeChange(digits.toIntOrNull())
        },
        label = { Text("درجتك في المادة") },
        suffix = { Text("من 100") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    )
}

/** تنبيه بسيط عند وجود فرق كبير بين التقدم والدرجة. */
@Composable
private fun InsightCard(text: String, warning: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (warning) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.tertiaryContainer
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (warning) Icons.Filled.Warning else Icons.Filled.Info,
                contentDescription = null,
                tint = if (warning) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onTertiaryContainer
                },
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = if (warning) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onTertiaryContainer
                },
            )
        }
    }
}

/**
 * تحليل بسيط للفرق بين تقدم المنهج والدرجة (منطق عرض فقط — لا يمس حساب التقدم).
 */
private fun gradeInsight(progressPercent: Int, grade: Int?): String? {
    if (grade == null) return null
    return when {
        progressPercent < 50 && grade < 60 ->
            "تقدمك في المنهج منخفض ودرجتك منخفضة — تأخرك في الدروس قد يكون مؤثرًا على درجاتك. راجع الدروس الفائتة وابدأ بالتعويض."

        progressPercent + 25 <= grade ->
            "درجتك أعلى من تقدمك في المنهج بفارق ملحوظ — حافظ على مستواك وحاول ألا تتأخر أكثر حتى لا تتأثر درجاتك القادمة."

        else -> null
    }
}

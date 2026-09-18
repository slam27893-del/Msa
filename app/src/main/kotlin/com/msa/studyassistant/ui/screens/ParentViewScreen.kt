// PROOF OF CONCEPT - MOCK DATA
// شاشة ولي الأمر (عرض فقط): لا تسجيل حساب حقيقي ولا أي صلاحية تعديل —
// عرض توضيحي للقراءة فقط لبيانات الطالب المحلية.

@file:OptIn(ExperimentalMaterial3Api::class)

package com.msa.studyassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.studyassistant.data.GradesStore
import com.msa.studyassistant.ui.MainViewModel
import com.msa.studyassistant.ui.components.SubjectDot
import com.msa.studyassistant.ui.components.formatShortDate

/**
 * عرض ولي الأمر — للقراءة فقط:
 * نسبة التقدم لكل مادة + الدروس الفائتة + الدرجات المُدخلة يدويًا.
 * لا تحتوي على أي عنصر تعديل أو تسجيل.
 */
@Composable
fun ParentViewScreen(
    viewModel: MainViewModel,
    gradesStore: GradesStore,
    onBack: () -> Unit,
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val grades by gradesStore.grades.collectAsStateWithLifecycle()
    val overall = progress.overall

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ولي الأمر — عرض فقط") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "banner") {
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
                            text = "عرض للقراءة فقط — لا يمكن التعديل من هذه الشاشة.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }

            item(key = "overall") {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "التقدم الإجمالي للفصل",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${overall.percent}%",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "${overall.completedLessons} من ${overall.totalLessons} دروس مكتملة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        LinearProgressIndicator(
                            progress = { overall.percent / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item(key = "subjects-title") {
                Text(
                    text = "التقدم في المواد",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            items(viewModel.subjects, key = { "p-${it.id}" }) { subject ->
                val subjectProgress = progress.subjectProgress[subject.id]
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SubjectDot(subject.id)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                            )
                            Text(
                                text = "${subjectProgress?.percent ?: 0}%",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (subjectProgress?.percent ?: 0) / 100f },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            text = "${subjectProgress?.completedLessons ?: 0} من ${subjectProgress?.totalLessons ?: subject.totalLessons} دروس",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            item(key = "missed-title") {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.padding(top = 10.dp))
                    Text(
                        text = "الدروس الفائتة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
            if (progress.missedLessons.isEmpty()) {
                item(key = "missed-empty") {
                    Text(
                        text = "لا توجد دروس فائتة.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(progress.missedLessons, key = { "m-${it.subjectId}-${it.lessonIndex}" }) { missed ->
                    val subject = viewModel.subjectById(missed.subjectId)
                    val lesson = subject?.lessonAt(missed.lessonIndex)
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = subject?.name ?: missed.subjectId,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Text(
                                text = lesson?.title ?: "",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Text(
                                text = "فات في: ${missed.missedDates.joinToString("، ") { formatShortDate(it) }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }
                }
            }

            item(key = "grades-title") {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.padding(top = 10.dp))
                    Text(
                        text = "الدرجات المُدخلة",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "تُدخل يدويًا من تبويب «الدرجات» (بيانات تجريبية).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(viewModel.subjects, key = { "g-${it.id}" }) { subject ->
                val grade = grades[subject.id]
                val subjectProgress = progress.subjectProgress[subject.id]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SubjectDot(subject.id)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = if (grade == null) "غير مُدخلة" else "$grade من 100",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (grade == null) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.tertiary
                        },
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "تقدم ${subjectProgress?.percent ?: 0}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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
    }
}

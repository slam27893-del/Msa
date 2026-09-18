@file:OptIn(ExperimentalMaterial3Api::class)

package com.msa.studyassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.studyassistant.ui.MainViewModel
import com.msa.studyassistant.ui.components.EmptyState
import com.msa.studyassistant.ui.components.OpenChevron
import com.msa.studyassistant.ui.components.formatShortDate

/**
 * صفحة «الدروس الفائتة»:
 * الدروس التي سُجّل غياب الطالب عنها ولم تُسجَّل أُخذت لاحقًا.
 */
@Composable
fun MissedLessonsScreen(
    viewModel: MainViewModel,
    onOpenLesson: (String, Int) -> Unit,
) {
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val missedLessons = progress.missedLessons

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "title") {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "الدروس الفائتة",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "دروس تغيبت عنها ولم تُسجَّل أُخذت لاحقًا.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (missedLessons.isEmpty()) {
            item(key = "empty") {
                EmptyState(
                    title = "ما فاتك شيء",
                    subtitle = "أي درس تسجّل غيابًا عنه يظهر هنا حتى تعوّضه.",
                )
            }
        } else {
            items(missedLessons, key = { "${it.subjectId}-${it.lessonIndex}" }) { missed ->
                val subject = viewModel.subjectById(missed.subjectId)
                val lesson = subject?.lessonAt(missed.lessonIndex)
                Card(
                    onClick = { onOpenLesson(missed.subjectId, missed.lessonIndex) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = subject?.name ?: missed.subjectId,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Text(
                                text = lesson?.title ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Text(
                                text = "فاتك في: ${missed.missedDates.joinToString("، ") { formatShortDate(it) }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                        OpenChevron(tint = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }
            item(key = "hint") {
                Text(
                    text = "علّم الدرس كمأخوذ من صفحة المادة (تحديد ← تم أخذ الدروس المحددة) ليختفي من هنا.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@file:OptIn(ExperimentalMaterial3Api::class)

package com.msa.studyassistant.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.studyassistant.domain.SubjectProgress
import com.msa.studyassistant.model.CurriculumSubject
import com.msa.studyassistant.model.LessonEntry
import com.msa.studyassistant.ui.MainViewModel
import com.msa.studyassistant.ui.components.LessonStatusBadge
import com.msa.studyassistant.ui.components.LessonDisplayStatus
import com.msa.studyassistant.ui.components.OpenChevron
import com.msa.studyassistant.ui.components.lessonDisplayStatus

/**
 * شاشة المادة: الدرس الحالي/القادم + قائمة المنهج كاملًا.
 * تدعم «تحديد» لتعليم أكثر من درس كمأخوذ.
 */
@Composable
fun SubjectDetailScreen(
    viewModel: MainViewModel,
    subjectId: String,
    onBack: () -> Unit,
    onOpenLesson: (Int) -> Unit,
) {
    val subject: CurriculumSubject? = remember(subjectId) { viewModel.subjectById(subjectId) }
    if (subject == null) {
        com.msa.studyassistant.ui.components.EmptyState(title = "المادة غير موجودة")
        return
    }

    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val subjectProgress = progress.subjectProgress[subject.id]
        ?: SubjectProgress(subjectId = subject.id, currentLessonIndex = 0, totalLessons = subject.totalLessons)

    var selectMode by remember { mutableStateOf(false) }
    var selectedIndices by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subject.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            selectMode = !selectMode
                            selectedIndices = emptySet()
                        },
                    ) {
                        Text(if (selectMode) "إلغاء" else "تحديد")
                    }
                },
            )
        },
        bottomBar = {
            if (selectMode) {
                BottomAppBar {
                    Text(
                        text = "${selectedIndices.size} درس محدد",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                    )
                    FilledTonalButton(
                        enabled = selectedIndices.isNotEmpty(),
                        onClick = {
                            viewModel.markLessonsTaken(subject.id, selectedIndices)
                            selectedIndices = emptySet()
                            selectMode = false
                        },
                    ) {
                        Text("تم أخذ الدروس المحددة")
                    }
                    Spacer(Modifier.width(12.dp))
                }
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            item(key = "header") {
                SubjectHeaderCard(subject = subject, subjectProgress = subjectProgress)
            }
            val blocks = subject.lessonEntries.groupBy { it.unit }
            blocks.forEach { (unit, entries) ->
                item(key = unit.id) {
                    Text(
                        text = unit.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 14.dp, bottom = 4.dp),
                    )
                }
                items(entries, key = { it.lesson.id }) { entry ->
                    LessonRow(
                        entry = entry,
                        status = lessonDisplayStatus(subjectProgress, entry.globalIndex),
                        selectMode = selectMode,
                        isSelected = entry.globalIndex in selectedIndices,
                        onClick = {
                            if (selectMode) {
                                selectedIndices = if (entry.globalIndex in selectedIndices) {
                                    selectedIndices - entry.globalIndex
                                } else {
                                    selectedIndices + entry.globalIndex
                                }
                            } else {
                                onOpenLesson(entry.globalIndex)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectHeaderCard(subject: CurriculumSubject, subjectProgress: SubjectProgress) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            val currentLesson = subject.lessonAt(subjectProgress.currentLessonIndex)
            val nextLesson = subjectProgress.nextLessonIndex?.let { subject.lessonAt(it) }
            Text(
                text = "الدرس الحالي",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = currentLesson?.title ?: "أنهيت دروس هذه المادة.",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = nextLesson?.let { "الدرس القادم: ${it.title}" } ?: "هذا آخر درس في المنهج.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { subjectProgress.percent / 100f },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "${subjectProgress.completedLessons} من ${subjectProgress.totalLessons} دروس • ${subjectProgress.percent}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun LessonRow(
    entry: LessonEntry,
    status: LessonDisplayStatus,
    selectMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selectMode) {
            Checkbox(checked = isSelected, onCheckedChange = null)
            Spacer(Modifier.width(8.dp))
        }
        LessonStatusBadge(status)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(text = entry.lesson.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "الدرس ${entry.globalIndex + 1} • ${entry.unit.title}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!selectMode) {
            OpenChevron()
        }
    }
}

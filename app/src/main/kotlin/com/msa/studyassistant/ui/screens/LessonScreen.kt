package com.msa.studyassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.studyassistant.domain.SubjectProgress
import com.msa.studyassistant.ui.MainViewModel
import com.msa.studyassistant.ui.components.BulletRow
import com.msa.studyassistant.ui.components.EmptyState
import com.msa.studyassistant.ui.components.LessonDisplayStatus
import com.msa.studyassistant.ui.components.LessonStatusBadge
import com.msa.studyassistant.ui.components.lessonDisplayStatus

/**
 * شاشة الدرس: بيانات تجريبية (أهداف، مفاهيم، ملخص) + إجراءات
 * «تم أخذ هذا الدرس» و«اجعله الدرس الحالي» (مع تأكيد إذا كان بعيدًا).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LessonScreen(
    viewModel: MainViewModel,
    subjectId: String,
    lessonIndex: Int,
    onBack: () -> Unit,
) {
    val subject = remember(subjectId) { viewModel.subjectById(subjectId) }
    val lesson = subject?.lessonAt(lessonIndex)
    val unit = subject?.unitForGlobalIndex(lessonIndex)
    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val subjectProgress = progress.subjectProgress[subjectId]
        ?: SubjectProgress(subjectId = subjectId, currentLessonIndex = 0, totalLessons = subject?.totalLessons ?: 0)

    var showJumpDialog by remember { mutableStateOf(false) }

    if (subject == null || lesson == null) {
        EmptyState(title = "الدرس غير موجود")
        return
    }

    val status = lessonDisplayStatus(subjectProgress, lessonIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = lesson.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                LessonStatusBadge(status)
                unit?.let {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            InfoCard(title = "أهداف الدرس") {
                lesson.objectives.forEach { objective -> BulletRow(objective) }
            }

            InfoCard(title = "المفاهيم الأساسية") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    lesson.keyConcepts.forEach { concept ->
                        AssistChip(onClick = {}, label = { Text(concept) })
                    }
                }
            }

            InfoCard(title = "ملخص") {
                Text(text = lesson.summary, style = MaterialTheme.typography.bodyMedium)
            }

            if (status != LessonDisplayStatus.TAKEN && !subjectProgress.isFinished) {
                Button(
                    onClick = { viewModel.markLessonsTaken(subjectId, listOf(lessonIndex)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(text = "تم أخذ هذا الدرس", style = MaterialTheme.typography.titleSmall)
                }
            }

            if (lessonIndex != subjectProgress.currentLessonIndex) {
                OutlinedButton(
                    onClick = {
                        if (viewModel.isSignificantJump(subjectId, lessonIndex)) {
                            showJumpDialog = true
                        } else {
                            viewModel.setCurrentLesson(subjectId, lessonIndex)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(text = "اجعله الدرس الحالي", style = MaterialTheme.typography.titleSmall)
                }
            } else {
                Text(
                    text = "هذا هو الدرس الحالي في هذه المادة.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = { Text("تأكيد تغيير الدرس الحالي") },
            text = {
                Text(
                    "الدرس المختار يبعد ${viewModel.jumpDistance(subjectId, lessonIndex)} دروس عن درسك الحالي، " +
                        "وسيُعتبر ما قبله مأخوذًا. هل أنت متأكد؟",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.setCurrentLesson(subjectId, lessonIndex)
                        showJumpDialog = false
                    },
                ) {
                    Text("نعم، عيّنه")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("رجوع")
                }
            },
        )
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            content()
        }
    }
}

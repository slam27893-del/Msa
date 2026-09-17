@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.msa.studyassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.studyassistant.R
import com.msa.studyassistant.data.ThemeStore
import com.msa.studyassistant.domain.StudentProgress
import com.msa.studyassistant.domain.SubjectProgress
import com.msa.studyassistant.model.AttendanceChoice
import com.msa.studyassistant.model.CurriculumSubject
import com.msa.studyassistant.ui.MainViewModel
import com.msa.studyassistant.ui.components.EmptyState
import com.msa.studyassistant.ui.components.SubjectDot
import com.msa.studyassistant.ui.components.formatFullDate
import com.msa.studyassistant.ui.components.label
import java.time.LocalDate

/**
 * الصفحة الرئيسية — التركيز على «وش عندك غدًا؟»
 * ثم قسم «اليوم» مع خيارات الحالة لكل مادة وزر «حضرت اليوم».
 */
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    themeStore: ThemeStore,
    onEditSchedule: () -> Unit,
    onOpenSubject: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()

    val today = remember { LocalDate.now() }
    val tomorrow = remember { today.plusDays(1) }

    val todaySubjects = remember(state.schedule) { viewModel.subjectsFor(state.schedule, today) }
    val tomorrowSubjects = remember(state.schedule) { viewModel.subjectsFor(state.schedule, tomorrow) }
    val savedChoices = remember(state.events) { viewModel.savedChoicesFor(today) }
    var choices by remember(todaySubjects, savedChoices) {
        mutableStateOf(viewModel.initialChoicesFor(todaySubjects, savedChoices))
    }
    val hasRecord = savedChoices.isNotEmpty()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") {
            HomeHeader(themeStore = themeStore, onEditSchedule = onEditSchedule)
        }

        // ------------------------------------------------------------
        // وش عندك غدًا؟
        // ------------------------------------------------------------
        item(key = "tomorrow-title") {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "وش عندك غدًا؟",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = formatFullDate(tomorrow),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (tomorrowSubjects.isEmpty()) {
            item(key = "tomorrow-empty") {
                if (state.schedule.isEmpty) {
                    EmptyState(
                        title = "ما عرفنا جدولك بعد",
                        subtitle = "أضف مواد أيام الأسبوع عشان نعرف وش عندك غدًا.",
                    )
                } else {
                    EmptyState(title = "ما عندك شيء غدًا", subtitle = "يوم مريح بلا مواد مجدولة.")
                }
            }
        } else {
            items(tomorrowSubjects, key = { "tmr-${it.id}" }) { subject ->
                TomorrowSubjectCard(
                    subject = subject,
                    subjectProgress = progress.subjectProgress[subject.id],
                    onClick = { onOpenSubject(subject.id) },
                )
            }
        }

        // ------------------------------------------------------------
        // اليوم + تسجيل الحضور
        // ------------------------------------------------------------
        item(key = "today-title") {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "اليوم",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = formatFullDate(today),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "غيابك ما يعني الدرس انمسح — يُحفظ في «الدروس الفائتة» حتى تعوّضه.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (todaySubjects.isEmpty()) {
            item(key = "today-empty") {
                EmptyState(title = "لا توجد مواد مجدولة اليوم")
            }
        } else {
            items(todaySubjects, key = { "tdy-${it.id}" }) { subject ->
                TodaySubjectCard(
                    subject = subject,
                    subjectProgress = progress.subjectProgress[subject.id],
                    selected = choices[subject.id] ?: AttendanceChoice.TAKEN,
                    onSelected = { choice -> choices = choices + (subject.id to choice) },
                )
            }
            item(key = "attend-button") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.recordDay(today, choices) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(text = "حضرت اليوم", style = MaterialTheme.typography.titleMedium)
                    }
                    if (hasRecord) {
                        Text(
                            text = "تم تسجيل اليوم — عدّل أي مادة ثم اضغط الزر لتحديث السجل.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(themeStore: ThemeStore, onEditSchedule: () -> Unit) {
    val themeMode by themeStore.mode.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(
                text = "المساعد الدراسي الذكي",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "نسخة تجريبية",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onEditSchedule) {
            Icon(Icons.Filled.DateRange, contentDescription = "تعديل الجدول")
        }
        androidx.compose.foundation.layout.Box {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    painter = painterResource(R.drawable.ic_theme_contrast),
                    contentDescription = "المظهر",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                com.msa.studyassistant.data.ThemeMode.entries.forEach { mode ->
                    DropdownMenuItem(
                        text = { Text(mode.label) },
                        onClick = {
                            themeStore.setMode(mode)
                            menuExpanded = false
                        },
                        trailingIcon = {
                            if (mode == themeMode) {
                                Icon(Icons.Filled.Check, contentDescription = null)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TomorrowSubjectCard(
    subject: CurriculumSubject,
    subjectProgress: SubjectProgress?,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubjectDot(subject.id)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            val currentLesson = subject.lessonAt(subjectProgress?.currentLessonIndex ?: 0)
            if (subjectProgress?.isFinished == true || currentLesson == null) {
                Text(
                    text = "أنهيت دروس هذه المادة.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = "الدرس القادم: ${currentLesson.title}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = subject.unitForGlobalIndex(subjectProgress?.currentLessonIndex ?: 0)?.title.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TodaySubjectCard(
    subject: CurriculumSubject,
    subjectProgress: SubjectProgress?,
    selected: AttendanceChoice,
    onSelected: (AttendanceChoice) -> Unit,
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SubjectDot(subject.id)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    val currentLesson = subject.lessonAt(subjectProgress?.currentLessonIndex ?: 0)
                    Text(
                        text = when {
                            subjectProgress?.isFinished == true || currentLesson == null -> "أنهيت دروس هذه المادة."
                            else -> "الدرس الحالي: ${currentLesson.title}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AttendanceChoice.entries.forEach { choice ->
                    FilterChip(
                        selected = selected == choice,
                        onClick = { onSelected(choice) },
                        label = { Text(choice.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = when (choice) {
                                AttendanceChoice.TAKEN -> MaterialTheme.colorScheme.primaryContainer
                                AttendanceChoice.NOT_TAKEN -> MaterialTheme.colorScheme.secondaryContainer
                                AttendanceChoice.ABSENT -> MaterialTheme.colorScheme.errorContainer
                            },
                            selectedLabelColor = when (choice) {
                                AttendanceChoice.TAKEN -> MaterialTheme.colorScheme.onPrimaryContainer
                                AttendanceChoice.NOT_TAKEN -> MaterialTheme.colorScheme.onSecondaryContainer
                                AttendanceChoice.ABSENT -> MaterialTheme.colorScheme.onErrorContainer
                            },
                        ),
                    )
                }
            }
        }
    }
}

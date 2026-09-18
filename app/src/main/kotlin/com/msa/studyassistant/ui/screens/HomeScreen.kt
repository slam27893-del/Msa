@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.msa.studyassistant.domain.SubjectProgress
import com.msa.studyassistant.model.AttendanceChoice
import com.msa.studyassistant.model.CurriculumSubject
import com.msa.studyassistant.ui.MainViewModel
import com.msa.studyassistant.ui.components.EmptyState
import com.msa.studyassistant.ui.components.OpenChevron
import com.msa.studyassistant.ui.components.SubjectDot
import com.msa.studyassistant.ui.components.formatFullDate
import com.msa.studyassistant.ui.components.label
import java.time.LocalDate
import kotlinx.coroutines.launch

/**
 * الصفحة الرئيسية:
 *
 * قسم «وش عندك غدًا؟» — قسم التحضير القادم، بارز داخل بطاقة ملونة،
 * يعرض لكل مادة درسها الحالي (المتوقع غدًا) والدرس القادم بعده.
 *
 * قسم «اليوم» — لتسجيل ما حدث في حصص اليوم:
 * حالة لكل مادة مع شرح مختصر لتأثيرها، وزر «حضرت اليوم»
 * يعرض نافذة تأكيد قبل التسجيل، ورسالة نجاح مع إمكانية التراجع بعده.
 */
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    themeStore: ThemeStore,
    onOpenSettings: () -> Unit,
    onOpenSubject: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val progress by viewModel.progress.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val today = remember { LocalDate.now() }
    val tomorrow = remember { today.plusDays(1) }

    val todaySubjects = remember(state.schedule) { viewModel.subjectsFor(state.schedule, today) }
    val tomorrowSubjects = remember(state.schedule) { viewModel.subjectsFor(state.schedule, tomorrow) }
    val savedChoices = remember(state.events) { viewModel.savedChoicesFor(today) }
    var choices by remember(todaySubjects, savedChoices) {
        mutableStateOf(viewModel.initialChoicesFor(todaySubjects, savedChoices))
    }
    val hasRecord = savedChoices.isNotEmpty()
    var showConfirmDialog by remember { mutableStateOf(false) }

    fun performDayRecording() {
        viewModel.recordDay(today, choices)
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "تم تسجيل دروس اليوم بنجاح",
                actionLabel = "تراجع",
                duration = SnackbarDuration.Long,
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoLastDayRecording()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "header") {
                HomeHeader(themeStore = themeStore, onOpenSettings = onOpenSettings)
            }

            // ------------------------------------------------------------
            // الغد: قسم التحضير القادم (بارز بصريًا)
            // ------------------------------------------------------------
            item(key = "tomorrow-section") {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "وش عندك غدًا؟",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = "حضّر دروسك القادمة",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Text(
                            text = formatFullDate(tomorrow),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(Modifier.height(4.dp))
                        if (tomorrowSubjects.isEmpty()) {
                            Text(
                                text = if (state.schedule.isEmpty) {
                                    "ما عرفنا جدولك بعد — أضف مواد أيام الأسبوع من الإعدادات لنعرف وش عندك غدًا."
                                } else {
                                    "ما عندك شيء غدًا — يوم مريح بلا مواد مجدولة."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        } else {
                            tomorrowSubjects.forEach { subject ->
                                TomorrowSubjectCard(
                                    subject = subject,
                                    subjectProgress = progress.subjectProgress[subject.id],
                                    onClick = { onOpenSubject(subject.id) },
                                )
                            }
                        }
                    }
                }
            }

            // ------------------------------------------------------------
            // اليوم: تسجيل ما حدث في الحصص
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
                        text = "سجّل ما حدث في حصص اليوم — أو اضغط «حضرت اليوم» لتسجيلها كلها كمأخوذة.",
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
                            onClick = { showConfirmDialog = true },
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
                                text = "اليوم مسجَّل — عدّل حالات أي مادة ثم اضغط الزر لتحديث السجل.",
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (showConfirmDialog && todaySubjects.isNotEmpty()) {
        ConfirmDayRecordingDialog(
            subjects = todaySubjects,
            choices = choices,
            onConfirm = {
                showConfirmDialog = false
                performDayRecording()
            },
            onDismiss = { showConfirmDialog = false },
        )
    }
}

@Composable
private fun HomeHeader(themeStore: ThemeStore, onOpenSettings: () -> Unit) {
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
        IconButton(onClick = onOpenSettings) {
            Icon(Icons.Filled.Settings, contentDescription = "الإعدادات")
        }
        Box {
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

/**
 * بطاقة مادة في قسم الغد: تعرض الدرس الحالي (المتوقع أخذه غدًا)
 * والوحدة، والدرس القادم بعده كمعلومة إضافية للتحضير.
 */
@Composable
private fun TomorrowSubjectCard(
    subject: CurriculumSubject,
    subjectProgress: SubjectProgress?,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
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
                        text = "الدرس الحالي: ${currentLesson.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = subject.unitForGlobalIndex(subjectProgress?.currentLessonIndex ?: 0)?.title.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    subjectProgress?.nextLessonIndex?.let { nextIndex ->
                        subject.lessonAt(nextIndex)?.let { next ->
                            Text(
                                text = "الدرس القادم: ${next.title}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            OpenChevron()
        }
    }
}

/** بطاقة مادة في قسم اليوم: الحالة الحالية + خيارات التسجيل مع شرح مختصر لتأثير كل خيار. */
@Composable
private fun TodaySubjectCard(
    subject: CurriculumSubject,
    subjectProgress: SubjectProgress?,
    selected: AttendanceChoice,
    onSelected: (AttendanceChoice) -> Unit,
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
                        leadingIcon = {
                            if (selected == choice) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        },
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
            Text(
                text = choiceCaption(selected),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** شرح مختصر لتأثير كل حالة — يظهر تحت خيارات المادة مباشرة. */
private fun choiceCaption(choice: AttendanceChoice): String = when (choice) {
    AttendanceChoice.TAKEN -> "سيتقدم الدرس الحالي إلى الدرس التالي."
    AttendanceChoice.NOT_TAKEN -> "سيبقى هذا الدرس كدرسك الحالي."
    AttendanceChoice.ABSENT -> "لن يتقدم الدرس، وسيظهر ضمن الدروس الفائتة."
}

/** نافذة تأكيد «حضرت اليوم»: تعرض بالضبط ما الذي سيُسجَّل قبل التنفيذ. */
@Composable
private fun ConfirmDayRecordingDialog(
    subjects: List<CurriculumSubject>,
    choices: Map<String, AttendanceChoice>,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val anyModified = subjects.any { (choices[it.id] ?: AttendanceChoice.TAKEN) != AttendanceChoice.TAKEN }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل دروس اليوم") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!anyModified) {
                    Text("سيتم تسجيل المواد التالية على أنها أُخذت اليوم:")
                    subjects.forEach { subject ->
                        Text(
                            text = "• ${subject.name}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    Text("سيتم تسجيل حالات اليوم كما اخترت:")
                    subjects.forEach { subject ->
                        val choice = choices[subject.id] ?: AttendanceChoice.TAKEN
                        Text(
                            text = "• ${subject.name}: ${choice.label}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                Text(
                    text = "هل تريد المتابعة؟",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("تسجيل الدروس")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
    )
}

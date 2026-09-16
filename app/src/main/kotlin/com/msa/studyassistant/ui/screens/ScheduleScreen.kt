package com.msa.studyassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.studyassistant.model.WeekDay
import com.msa.studyassistant.model.WeeklySchedule
import com.msa.studyassistant.ui.MainViewModel

/**
 * شاشة الجدول الأسبوعي: تحديد المواد الموجودة في كل يوم (بدون أوقات حصص في هذه النسخة).
 * تُستخدم في الإعداد الأول، ويمكن فتحها لاحقًا للتعديل من الصفحة الرئيسية.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    viewModel: MainViewModel,
    onboarding: Boolean,
    onDone: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // تحديدات كل يوم (معرفات المواد) — تبدأ من الجدول المحفوظ إن وجد.
    val selections = remember(state.schedule) {
        mutableStateMapOf<WeekDay, Set<String>>().apply {
            WeekDay.entries.forEach { day ->
                put(day, state.schedule.subjectsOn(day).toSet())
            }
        }
    }
    var activeDay by remember { mutableStateOf(WeekDay.SUNDAY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
    ) {
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Spacer(Modifier.height(20.dp))
            Text(
                text = if (onboarding) "جدولك الأسبوعي" else "تعديل الجدول",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "حدّد المواد الموجودة في كل يوم من أيام الأسبوع.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(12.dp))
        ScrollableTabRow(
            selectedTabIndex = WeekDay.entries.indexOf(activeDay),
            edgePadding = 20.dp,
        ) {
            WeekDay.entries.forEach { day ->
                Tab(
                    selected = day == activeDay,
                    onClick = { activeDay = day },
                    text = { Text(day.displayName) },
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(
            Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "مواد يوم ${activeDay.displayName}:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                viewModel.subjects.forEach { subject ->
                    val checked = subject.id in (selections[activeDay] ?: emptySet())
                    FilterChip(
                        selected = checked,
                        onClick = {
                            val current = selections[activeDay] ?: emptySet()
                            selections[activeDay] = if (checked) current - subject.id else current + subject.id
                        },
                        label = { Text(subject.name) },
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (onboarding) {
                    OutlinedButton(
                        onClick = onDone,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text("تخطي الآن")
                    }
                }
                Button(
                    onClick = {
                        val map = WeekDay.entries.associateWith { day -> selections[day]?.toList() ?: emptyList() }
                        viewModel.saveSchedule(WeeklySchedule(subjectsByDay = map))
                        onDone()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text("حفظ الجدول", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

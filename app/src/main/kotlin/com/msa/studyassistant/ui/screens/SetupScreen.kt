package com.msa.studyassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msa.studyassistant.model.GradeLevel
import com.msa.studyassistant.model.Semester
import com.msa.studyassistant.model.Stage
import com.msa.studyassistant.model.StudentProfile
import com.msa.studyassistant.model.StudyTrack

/**
 * شاشة إعداد الطالب: مرحلة/صف/فصل/مسار.
 * القيم حاليًا ثابتة (Prototype أول ثانوي - فصل أول) وقابلة للتوسعة من تعريفات النماذج.
 */
@Composable
fun SetupScreen(onStart: (StudentProfile) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = "المساعد الدراسي الذكي",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "لنجهّز حسابك أولًا — الخطوة بسيطة.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))

        SetupField(label = "المرحلة", options = Stage.entries.map { it.displayName })
        SetupField(label = "الصف", options = GradeLevel.entries.map { it.displayName })
        SetupField(label = "الفصل الدراسي", options = Semester.entries.map { it.displayName })
        SetupField(
            label = "المسار",
            options = StudyTrack.entries.map { it.displayName },
            note = "خيارات المسارات تُفعَّل في نسخة قادمة.",
        )

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onStart(StudentProfile()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(text = "التالي", style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = "يمكنك تعديل هذه الإعدادات لاحقًا.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SetupField(label: String, options: List<String>, note: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = option == options.firstOrNull(),
                    onClick = {},
                    label = { Text(option) },
                )
            }
        }
        if (note != null) {
            Text(
                text = note,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

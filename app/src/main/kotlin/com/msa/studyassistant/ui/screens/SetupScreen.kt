@file:OptIn(ExperimentalMaterial3Api::class)

package com.msa.studyassistant.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
 * القيم تُعرض كبيانات محددة ومعتمدة (وليست أزرارًا) لأنها الخيارات
 * المتاحة حاليًا في الـ Prototype — ويمكن مراجعتها وتعديل الجدول
 * لاحقًا في أي وقت من شاشة الإعدادات.
 */
@Composable
fun SetupScreen(onStart: (StudentProfile) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
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
        Spacer(Modifier.height(4.dp))

        SetupField(label = "المرحلة", value = Stage.SECONDARY.displayName)
        SetupField(label = "الصف", value = GradeLevel.FIRST_SECONDARY.displayName)
        SetupField(label = "الفصل الدراسي", value = Semester.FIRST.displayName)
        SetupField(
            label = "المسار",
            value = StudyTrack.GENERAL.displayName,
            note = "خيارات المسارات تُفعَّل في نسخة قادمة.",
        )

        Spacer(Modifier.height(4.dp))
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
            text = "يمكنك مراجعة هذه البيانات وتعديل جدولك لاحقًا من الإعدادات.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** صف بيانات محددة (غير قابل للنقر) — يوضح القيمة المعتمدة بوضوح دون أن يبدو زرًا. */
@Composable
private fun SetupField(label: String, value: String, note: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
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

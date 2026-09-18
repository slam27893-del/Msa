package com.msa.studyassistant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.msa.studyassistant.domain.SubjectProgress
import com.msa.studyassistant.model.AttendanceChoice
import com.msa.studyassistant.ui.theme.subjectDotColor
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * مكونات وواجهات مساعدة مشتركة بين الشاشات.
 */

/** حالة عرض الدرس في القوائم (مشتقة من تقدم المادة). */
enum class LessonDisplayStatus(val label: String) {
    TAKEN("مأخوذ"),
    CURRENT("الدرس الحالي"),
    UPCOMING("لم يُؤخذ بعد"),
    MISSED("فائت"),
}

fun lessonDisplayStatus(progress: SubjectProgress, lessonIndex: Int): LessonDisplayStatus = when {
    lessonIndex < progress.currentLessonIndex -> LessonDisplayStatus.TAKEN
    lessonIndex in progress.missedLessonIndices -> LessonDisplayStatus.MISSED
    lessonIndex == progress.currentLessonIndex -> LessonDisplayStatus.CURRENT
    else -> LessonDisplayStatus.UPCOMING
}

/** نصوص خيارات تسجيل اليوم. */
val AttendanceChoice.label: String
    get() = when (this) {
        AttendanceChoice.TAKEN -> "تم أخذ الدرس"
        AttendanceChoice.NOT_TAKEN -> "لم نأخذ الدرس"
        AttendanceChoice.ABSENT -> "غياب"
    }

private val arabicLocale: Locale = Locale.forLanguageTag("ar")
private val fullDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE، d MMMM", arabicLocale)
private val shortDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM", arabicLocale)

fun formatFullDate(date: LocalDate): String = date.format(fullDateFormatter)

fun formatShortDate(isoDate: String): String =
    runCatching { LocalDate.parse(isoDate).format(shortDateFormatter) }.getOrDefault(isoDate)

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier,
    )
}

@Composable
fun EmptyState(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun LessonStatusBadge(status: LessonDisplayStatus, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val container: Color
    val content: Color
    var icon: ImageVector? = null
    when (status) {
        LessonDisplayStatus.TAKEN -> {
            container = scheme.primaryContainer
            content = scheme.onPrimaryContainer
            icon = Icons.Filled.CheckCircle
        }
        LessonDisplayStatus.CURRENT -> {
            container = scheme.tertiaryContainer
            content = scheme.onTertiaryContainer
            icon = Icons.Filled.PlayArrow
        }
        LessonDisplayStatus.UPCOMING -> {
            container = scheme.surfaceVariant
            content = scheme.onSurfaceVariant
        }
        LessonDisplayStatus.MISSED -> {
            container = scheme.errorContainer
            content = scheme.onErrorContainer
            icon = Icons.Filled.Warning
        }
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = content)
            Spacer(Modifier.width(4.dp))
        }
        Text(status.label, style = MaterialTheme.typography.labelMedium, color = content)
    }
}

@Composable
fun SubjectDot(subjectId: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(subjectDotColor(subjectId)),
    )
}

@Composable
fun BulletRow(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        )
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

/**
 * سهم صغير يشير إلى أن البطاقة قابلة للفتح.
 * يشير لليسار لأن الواجهة عربية RTL (اتجاه التقدم للأمام).
 */
@Composable
fun OpenChevron(
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Filled.KeyboardArrowLeft,
        contentDescription = null,
        tint = tint,
        modifier = modifier,
    )
}

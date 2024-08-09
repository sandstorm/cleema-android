package de.cleema.android.core.components

import android.app.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import de.cleema.android.core.common.formatted
import java.time.format.FormatStyle

@Composable
fun DatePicker(
    value: kotlinx.datetime.LocalDate,
    onValueChange: (kotlinx.datetime.LocalDate) -> Unit,
) {
    val dialog = DatePickerDialog(
        LocalContext.current,
        { _, year, month, dayOfMonth ->
            onValueChange(kotlinx.datetime.LocalDate(year, month + 1, dayOfMonth))
        },
        value.year,
        value.monthNumber - 1,
        value.dayOfMonth,
    )
    TextButton(
        shape = MaterialTheme.shapes.medium,
        onClick = { dialog.show() }) {
        Text(
            text = value.formatted(FormatStyle.MEDIUM)
        )
    }
}

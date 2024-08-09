package de.cleema.android.core.components

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import de.cleema.android.core.styling.Action

@Composable
fun LinkButton(
    title: String,
    uriString: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default
) {
    val handler = LocalUriHandler.current

    ClickableText(
        text = AnnotatedString(
            title, SpanStyle(color = Action)
        ),
        onClick = {
            handler.openUri(uriString)
        },
        modifier = modifier,
        style = style
    )
}

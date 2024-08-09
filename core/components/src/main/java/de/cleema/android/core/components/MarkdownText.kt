/*
 * Created by Kumpels and Friends on 2023-01-16
 * Copyright © 2023 Kumpels and Friends. All rights reserved.
 */

package de.cleema.android.core.components

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.annotation.IdRes
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import coil.ImageLoader
import de.cleema.android.core.styling.Action
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin

data class MarkdownTextConfiguration(val scrollToTop: Boolean = false)

@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    linkColor: Color = Action,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    @FontRes fontResource: Int? = null,
    style: TextStyle = LocalTextStyle.current,
    @IdRes viewId: Int? = null,
    onClick: (() -> Unit)? = null,
    // this option will disable all clicks on links, inside the markdown text
    // it also enable the parent view to receive the click event
    disableLinkMovementMethod: Boolean = false,
    imageLoader: ImageLoader? = null,
    config: MarkdownTextConfiguration = MarkdownTextConfiguration()
) {
    val defaultColor: Color = LocalContentColor.current.copy(alpha = LocalContentColor.current.alpha)
    val context: Context = LocalContext.current
    val markdownRender: Markwon = remember {
        createMarkdownRender(context, imageLoader) { themeBuilder ->
            themeBuilder.linkColor(linkColor.toArgb())
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            createTextView(
                context = ctx,
                color = color,
                defaultColor = defaultColor,
                fontSize = fontSize,
                fontResource = fontResource,
                maxLines = maxLines,
                style = style,
                textAlign = textAlign,
                viewId = viewId,
                onClick = onClick,
            )
        },
        update = { textView ->
            markdownRender.setMarkdown(textView, markdown)
            if (disableLinkMovementMethod) {
                textView.movementMethod = null
            }
            if (config.scrollToTop) {
                textView.scrollY = 0
            }
        }
    )
}

private fun createTextView(
    context: Context,
    color: Color = Color.Unspecified,
    defaultColor: Color,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    @FontRes fontResource: Int? = null,
    style: TextStyle,
    @IdRes viewId: Int? = null,
    onClick: (() -> Unit)? = null
): TextView {
    val textColor = color.takeOrElse { style.color.takeOrElse { defaultColor } }
    val mergedStyle = style.merge(
        TextStyle(
            color = textColor,
            fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
            textAlign = textAlign,
        )
    )

    return TextView(context).apply {
        onClick?.let { setOnClickListener { onClick() } }
        setTextColor(textColor.toArgb())
        setMaxLines(maxLines)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, mergedStyle.fontSize.value)

        viewId?.let { id = viewId }
        textAlign?.let { align ->
            textAlignment = when (align) {
                TextAlign.Left, TextAlign.Start -> View.TEXT_ALIGNMENT_TEXT_START
                TextAlign.Right, TextAlign.End -> View.TEXT_ALIGNMENT_TEXT_END
                TextAlign.Center -> View.TEXT_ALIGNMENT_CENTER
                else -> View.TEXT_ALIGNMENT_TEXT_START
            }
        }

        fontResource?.let { font ->
            typeface = ResourcesCompat.getFont(context, font)
        }
    }
}

private fun createMarkdownRender(
    context: Context,
    imageLoader: ImageLoader?,
    configureTheme: (MarkwonTheme.Builder) -> Unit
): Markwon {
    val coilImageLoader = imageLoader ?: ImageLoader.Builder(context)
        .apply {
            crossfade(true)
        }.build()

    return Markwon.builder(context)
        .usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureTheme(builder: MarkwonTheme.Builder) = configureTheme(builder)
        })
        .usePlugin(HtmlPlugin.create())
        .usePlugin(CoilImagesPlugin.create(context, coilImageLoader))
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(TablePlugin.create(context))
//        .usePlugin(LinkifyPlugin.create())
        .build()
}

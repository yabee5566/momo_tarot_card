package com.onean.momo.ext

import android.os.Build.VERSION.SDK_INT
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.node.Ref
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.onean.momo.R

@Composable
fun SimpleImage(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center,
    tint: Color? = null,
    alpha: Float = 1F
) {
    Image(
        modifier = modifier,
        painter = painterResource(id = id),
        contentScale = contentScale,
        alignment = alignment,
        contentDescription = null,
        colorFilter = tint?.let { ColorFilter.tint(it) },
        alpha = alpha
    )
}

@Composable
fun GifImage(
    @DrawableRes id: Int,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = id).build(), imageLoader = imageLoader
        ),
        contentDescription = null
    )
}

@Composable
@NonRestartableComposable
fun SimpleAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    imageRequestBuilder: ImageRequest.Builder.() -> Unit = {},
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: Painter? = null,
    error: Painter? = placeholder,
    tint: Color? = null,
) {
    return when (model) {
        is Int -> {
            SimpleImage(
                id = model,
                modifier = modifier,
                tint = tint,
                contentScale = contentScale
            )
        }

        is ImageVector -> {
            Image(
                modifier = modifier,
                imageVector = model,
                contentDescription = null,
                colorFilter = tint?.let {
                    ColorFilter.tint(it)
                }
            )
        }

        else -> {
            val context = LocalContext.current
            val url = model
            val request = remember(context, url, imageRequestBuilder) {
                ImageRequest.Builder(context)
                    .data(url)
                    .apply(imageRequestBuilder)
                    .build()
            }
            AsyncImage(
                modifier = modifier,
                model = request,
                contentDescription = null,
                contentScale = contentScale,
                placeholder = if (isInPreview()) {
                    painterResource(R.drawable.ic_launcher_background)
                } else {
                    placeholder
                },
                error = error,
                colorFilter = tint?.let { ColorFilter.tint(it) },
                onError = {
                    // FIXME: should retry
                }
            )
        }
    }
}

@Composable
fun SimpleAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn(
        animationSpec = keyframes {
            this.durationMillis = 300
        }
    ),
    exit: ExitTransition = fadeOut(
        animationSpec = keyframes {
            this.durationMillis = 300
        }
    ),
    content: @Composable () -> Unit,
) {
    if (isInPreview() && visible) {
        content()
        return
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = enter,
        exit = exit
    ) {
        content()
    }
}

@Composable
inline fun <T> AnimatedNullableVisibility(
    value: T?,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = shrinkOut() + fadeOut(),
    crossinline content: @Composable (T) -> Unit
) {
    val ref = remember { Ref<T>() }
    ref.value = value ?: ref.value
    AnimatedVisibility(
        modifier = modifier,
        visible = value != null,
        enter = enter,
        exit = exit,
        content = {
            ref.value?.let { value ->
                content(value)
            }
        }
    )
}

@Composable
fun SimpleDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    horizontalPadding: Dp = 20.dp,
    verticalPadding: Dp = 30.dp,
    cornerRadius: Dp = 8.dp,
    properties: DialogProperties = DialogProperties(),
    content: @Composable BoxScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = properties,
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .padding(horizontal = horizontalPadding, vertical = verticalPadding),
            content = content,
        )
    }
}

@Deprecated("Use SimpleTextField with TextFieldState instead")
@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: @Composable () -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    maxLength: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    leadingIconAlignment: Alignment.Vertical = Alignment.CenterVertically,
    trailingIconAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Green,
        backgroundColor = Color.White
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            modifier = modifier,
            textStyle = textStyle,
            singleLine = singleLine,
            value = value,
            maxLines = maxLines,
            minLines = minLines,
            enabled = enabled,
            readOnly = readOnly,
            visualTransformation = visualTransformation,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            onValueChange = {
                if (it.length <= maxLength) {
                    onValueChange(it)
                } else {
                    val limitedValue = it.take(maxLength)
                    if (value != limitedValue) {
                        onValueChange(limitedValue)
                    }
                }
            },
            cursorBrush = SolidColor(Color.Green),
            decorationBox = { innerTextField ->
                val textField = @Composable {
                    if (value.isEmpty()) {
                        hint()
                    }
                    innerTextField()
                }
                if (leadingIcon != null || trailingIcon != null) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        leadingIcon?.let { leading ->
                            Box(modifier = Modifier.align(leadingIconAlignment)) {
                                leading()
                            }
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1F)
                        ) {
                            textField()
                        }

                        trailingIcon?.let { trailing ->
                            Box(modifier = Modifier.align(trailingIconAlignment)) {
                                trailing()
                            }
                        }
                    }
                } else {
                    textField()
                }
            }
        )
    }
}
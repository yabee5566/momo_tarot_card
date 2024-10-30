package com.onean.momo.ext

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.min

fun Modifier.safeClickable(
    debounceInterval: Long = 500,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    clickable(
        enabled = enabled,
        interactionSource = null,
        indication = null,
        onClick = {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastClickTime) < debounceInterval) return@clickable
            lastClickTime = currentTime
            onClick()
        }
    )
}

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = clickable(
    enabled = enabled,
    indication = null,
    onClickLabel = onClickLabel,
    role = role,
    interactionSource = null,
    onClick = onClick
)

fun Modifier.noRippleSafeClickable(
    debounceInterval: Long = 500,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    noRippleClickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
    ) {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastClickTime) < debounceInterval) return@noRippleClickable

        lastClickTime = currentTime
        onClick()
    }
}

fun Modifier.bounceClickable(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = remember { Animatable(1f) }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> scale.animateTo(0.97f)
                is PressInteraction.Release -> scale.animateTo(1f)
                is PressInteraction.Cancel -> scale.animateTo(1f)
            }
        }
    }
    Modifier
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}

fun Modifier.bounceSafeClickable(
    debounceInterval: Long = 500,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    bounceClickable(
        enabled = enabled,
        onClick = {
            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastClickTime) < debounceInterval) return@bounceClickable

            lastClickTime = currentTime
            onClick()
        }
    )
}

inline fun Modifier.conditional(condition: Boolean, block: Modifier.() -> Modifier) = if (condition) {
    then(block(Modifier))
} else {
    this
}

fun Modifier.easyPadding(
    start: Dp? = null,
    top: Dp? = null,
    end: Dp? = null,
    bottom: Dp? = null,
    horizontal: Dp? = null,
    vertical: Dp? = null,
    all: Dp? = null,
): Modifier {
    val allPadding = all ?: 0.dp
    val horizontalPadding = horizontal ?: allPadding
    val verticalPadding = vertical ?: allPadding
    val startPadding = start ?: horizontalPadding
    val endPadding = end ?: horizontalPadding
    val bottomPadding = bottom ?: verticalPadding
    val topPadding = top ?: verticalPadding
    return then(
        Modifier.padding(
            start = startPadding,
            end = endPadding,
            bottom = bottomPadding,
            top = topPadding,
        )
    )
}

fun Modifier.offsetParentPadding(
    horizontal: Dp? = null,
    vertical: Dp? = null,
): Modifier {
    return when {
        horizontal != null -> {
            this.layout { measurable, constraints ->
                val overridenWidth = constraints.maxWidth + 2 * horizontal.roundToPx()
                val placeable = measurable.measure(constraints.copy(maxWidth = overridenWidth))
                layout(placeable.width, placeable.height) {
                    placeable.place(0, 0)
                }
            }
        }

        vertical != null -> {
            this.layout { measurable, constraints ->
                val overridenHeight = constraints.maxHeight + 2 * vertical.roundToPx()
                val placeable = measurable.measure(constraints.copy(maxHeight = overridenHeight))
                layout(placeable.width, placeable.height) {
                    placeable.place(0, 0)
                }
            }
        }

        else -> {
            this
        }
    }
}

fun Modifier.nonClickable(): Modifier = clickable(
    enabled = false,
    onClick = {}
)

fun Modifier.advancedShadow(
    color: Color = Color.Black,
    alpha: Float = 0.1f,
    cornersRadius: Dp = 0.dp,
    shadowBlurRadius: Dp = 1.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp
) = drawWithCache {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparentColor = color.copy(alpha = 0f).toArgb()
    val paint = Paint()
    val frameworkPaint = paint.asFrameworkPaint()
    onDrawBehind {
        drawIntoCanvas {
            frameworkPaint.color = transparentColor
            frameworkPaint.setShadowLayer(
                shadowBlurRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                shadowColor
            )
            it.drawRoundRect(
                left = 0f,
                top = 0f,
                right = this.size.width,
                bottom = this.size.height,
                radiusX = cornersRadius.toPx(),
                radiusY = cornersRadius.toPx(),
                paint = paint
            )
        }
    }
}

// ref: https://issuetracker.google.com/issues/192433071
@OptIn(ExperimentalLayoutApi::class)
fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }
    if (isFocused) {
        val imeIsVisible = WindowInsets.isImeVisible
        val focusManager = LocalFocusManager.current
        LaunchedEffect(imeIsVisible) {
            if (imeIsVisible) {
                keyboardAppearedSinceLastFocused = true
            } else if (keyboardAppearedSinceLastFocused) {
                focusManager.clearFocus()
            }
        }
    }
    onFocusEvent {
        if (isFocused != it.isFocused) {
            isFocused = it.isFocused
            if (isFocused) {
                keyboardAppearedSinceLastFocused = false
            }
        }
    }
}

fun Modifier.imePaddingExcludingNavigationBars(): Modifier {
    return composed {
        windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
    }
}

/**
 * @return true if the blur modifier is supported on the current OS version.
 *
 * The docs say the `blur` modifier is only supported on Android 12+:
 * https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier#(androidx.compose.ui.Modifier).blur(androidx.compose.ui.unit.Dp,androidx.compose.ui.draw.BlurredEdgeTreatment)
 * */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun canUseBlur(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

fun Modifier.blurCompat(
    radius: Dp,
    edgeTreatment: BlurredEdgeTreatment = BlurredEdgeTreatment.Rectangle
): Modifier = when {
    radius.value == 0f -> this
    canUseBlur() -> blur(radius, edgeTreatment)
    else -> this.drawWithContent {
        this.drawContent()
        this.drawRect(Color.Black)
    }
}

fun Modifier.verticalFadingEdges(
    fadingEdgeLength: Dp,
    scrollState: ScrollState? = null,
): Modifier = this.then(
    Modifier.fadingEdges(
        fadingEdgeOrientation = FadingEdgeOrientation.VERTICAL,
        fadingEdgeLength = fadingEdgeLength,
        scrollState = scrollState,
    )
)

fun Modifier.horizontalFadingEdges(
    fadingEdgeLength: Dp,
    scrollState: ScrollState? = null,
): Modifier = this.then(
    Modifier.fadingEdges(
        fadingEdgeOrientation = FadingEdgeOrientation.HORIZONTAL,
        fadingEdgeLength = fadingEdgeLength,
        scrollState = scrollState,
    )
)

private enum class FadingEdgeOrientation {
    VERTICAL, HORIZONTAL
}

private fun Modifier.fadingEdges(
    fadingEdgeOrientation: FadingEdgeOrientation,
    fadingEdgeLength: Dp,
    scrollState: ScrollState? = null,
): Modifier = this.then(
    Modifier
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()

            val startColors = listOf(Color.Transparent, Color.Black)
            val startPoint = (scrollState?.value ?: 0).toFloat()
            val startGradientLength = if (scrollState != null) {
                min(fadingEdgeLength.toPx(), startPoint)
            } else {
                fadingEdgeLength.toPx()
            }

            when (fadingEdgeOrientation) {
                FadingEdgeOrientation.VERTICAL -> {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = startColors,
                            startY = startPoint,
                            endY = startPoint + startGradientLength
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }

                FadingEdgeOrientation.HORIZONTAL -> {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = startColors,
                            startX = startPoint,
                            endX = startPoint + startGradientLength
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
            }

            val endColors = listOf(Color.Black, Color.Transparent)
            val endPoint = when (fadingEdgeOrientation) {
                FadingEdgeOrientation.VERTICAL -> {
                    size.height - (scrollState?.maxValue ?: 0) + (scrollState?.value ?: 0)
                }

                FadingEdgeOrientation.HORIZONTAL -> {
                    size.width - (scrollState?.maxValue ?: 0) + (scrollState?.value ?: 0)
                }
            }
            val endGradientLength = if (scrollState != null) {
                min(fadingEdgeLength.toPx(), (scrollState.maxValue - scrollState.value).toFloat())
            } else {
                fadingEdgeLength.toPx()
            }

            when (fadingEdgeOrientation) {
                FadingEdgeOrientation.VERTICAL -> {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = endColors,
                            startY = endPoint - endGradientLength,
                            endY = endPoint
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }

                FadingEdgeOrientation.HORIZONTAL -> {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = endColors,
                            startX = endPoint - endGradientLength,
                            endX = endPoint
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
            }
        }
)

fun Modifier.onSizeChangedDp(onSizeChanged: (DpSize) -> Unit): Modifier {
    return composed {
        val density = LocalDensity.current
        onSizeChanged {
            with(density) {
                onSizeChanged(
                    DpSize(
                        width = it.width.toDp(),
                        height = it.height.toDp()
                    )
                )
            }
        }
    }
}

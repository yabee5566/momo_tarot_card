package com.onean.momo.ext
import android.app.Activity
import android.content.ContextWrapper
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun isInPreview(): Boolean = LocalInspectionMode.current

@Composable
fun <T> CollectFlowWithLifecycle(
    flow: () -> Flow<T>,
    minActiveState: Lifecycle.State = Lifecycle.State.RESUMED,
    onEvent: (T) -> Unit,
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val currentFlow by rememberUpdatedState(newValue = flow)
    val currentOnEvent by rememberUpdatedState(newValue = onEvent)
    LaunchedEffect(lifecycleOwner) {
        currentFlow().flowWithLifecycle(
            lifecycle = lifecycleOwner.lifecycle,
            minActiveState = minActiveState
        ).collect(currentOnEvent)
    }
}

@Composable
fun localActivity(): Activity {
    val context = LocalContext.current
    return remember(context) {
        context.let {
            var ctx = it
            while (ctx is ContextWrapper) {
                if (ctx is Activity) {
                    return@let ctx
                }
                ctx = ctx.baseContext
            }
            error("Expected an activity context: $ctx")
        }
    }
}

fun ComposeView.setContentInFragment(
    strategy: ViewCompositionStrategy = ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
    content: @Composable () -> Unit
) {
    setViewCompositionStrategy(strategy)
    setContent(content = content)
}

fun ComposeView.setContentInCustomView(
    content: @Composable () -> Unit,
) {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
    setContent(content)
}

// workaround for issues that the BackHandler will not properly intercept back presses after activity pause/resume
// ref: https://issuetracker.google.com/issues/182284739
@Composable
fun LifecycleBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    var refreshBackHandler by rememberSaveable { mutableStateOf(false) }
    OnLifecycle(
        onResume = {
            refreshBackHandler = true
        },
        onPause = {
            refreshBackHandler = false
        }
    )
    if (refreshBackHandler) {
        BackHandler(enabled = enabled, onBack = onBack)
    }
}

// Some view changes it's layout strategy based on it's layoutParams.
// We convert from Compose Modifier to layout params here.
fun BoxWithConstraintsScope.asViewGroupLayoutParams(): ViewGroup.LayoutParams {
    val width = if (constraints.hasFixedWidth) {
        ViewGroup.LayoutParams.MATCH_PARENT
    } else {
        ViewGroup.LayoutParams.WRAP_CONTENT
    }
    val height = if (constraints.hasFixedHeight) {
        ViewGroup.LayoutParams.MATCH_PARENT
    } else {
        ViewGroup.LayoutParams.WRAP_CONTENT
    }
    return ViewGroup.LayoutParams(width, height)
}

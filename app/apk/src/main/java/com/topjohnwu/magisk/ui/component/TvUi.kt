package com.topjohnwu.magisk.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * TV: request focus once when a screen first appears, so a remote has an immediate target.
 * Attach the returned requester to a stable element (usually the top-bar back button).
 * Uses [LaunchedEffect] so the request runs after composition and survives recomposition
 * without re-stealing focus from the user.
 */
@Composable
fun rememberTvInitialFocus(): FocusRequester {
    val requester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        runCatching { requester.requestFocus() }
    }
    return requester
}

@Composable
fun Modifier.tvFocusFrame(
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(18.dp),
): Modifier {
    if (!enabled) return this

    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.03f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "tvFocusScale",
    )
    val borderColor by animateColorAsState(
        targetValue = if (focused) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 100),
        label = "tvFocusBorder",
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (focused) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f)
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 100),
        label = "tvFocusBackground",
    )

    return this
        .onFocusChanged { focused = it.hasFocus }
        .zIndex(if (focused) 1f else 0f)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .background(backgroundColor, shape)
        .border(2.dp, borderColor, shape)
}

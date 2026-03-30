package com.willowtree.vocable.headpointer

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import com.willowtree.vocable.customviews.PointerListener
import kotlin.math.roundToInt

val LocalHeadPointerRegistry = androidx.compose.runtime.staticCompositionLocalOf<HeadPointerTargetRegistry> {
    error("LocalHeadPointerRegistry not provided")
}

/**
 * Registers [listener] for head-pointer hit-testing and updates window bounds from layout.
 */
fun Modifier.pointerHeadTarget(
    listener: PointerListener,
    enabled: Boolean = true,
): Modifier = composed {
    val registry = LocalHeadPointerRegistry.current
    val target = remember(listener) { ComposeHeadPointerTarget(listener) }
    val enabledState by rememberUpdatedState(enabled)

    DisposableEffect(listener, registry) {
        registry.register(target)
        onDispose { registry.unregister(target) }
    }

    SideEffect {
        target.setActiveCheck { enabledState }
    }

    Modifier.onGloballyPositioned { coords ->
        target.updateBoundsFromCoordinates(coords)
    }
}

private fun ComposeHeadPointerTarget.updateBoundsFromCoordinates(coords: LayoutCoordinates) {
    if (!coords.isAttached) return
    val pos: Offset = coords.positionInWindow()
    val size = coords.size
    val left = pos.x.roundToInt()
    val top = pos.y.roundToInt()
    val right = (pos.x + size.width).roundToInt()
    val bottom = (pos.y + size.height).roundToInt()
    updateBounds(left, top, right, bottom)
}

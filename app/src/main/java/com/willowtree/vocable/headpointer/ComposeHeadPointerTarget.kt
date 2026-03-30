package com.willowtree.vocable.headpointer

import android.graphics.Rect
import com.willowtree.vocable.customviews.PointerListener

/**
 * Hit target for head pointer driven by Compose [onGloballyPositioned] bounds in window coordinates.
 * Uses [Rect.contains] (left/top inclusive, right/bottom exclusive).
 */
class ComposeHeadPointerTarget(
    override val listener: PointerListener,
    private var activeCheck: () -> Boolean = { true },
) : HeadPointerTarget {

    private val rect = Rect()

    fun setActiveCheck(check: () -> Boolean) {
        activeCheck = check
    }

    fun updateBounds(left: Int, top: Int, right: Int, bottom: Int) {
        rect.set(left, top, right, bottom)
    }

    override val isActive: Boolean get() = activeCheck()

    override fun containsPointer(pointerCenterScreenX: Int, pointerCenterScreenY: Int): Boolean =
        isActive && rect.contains(pointerCenterScreenX, pointerCenterScreenY)
}

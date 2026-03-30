package com.willowtree.vocable.headpointer

import com.willowtree.vocable.customviews.PointerListener

/**
 * Something the head-tracked pointer can hover; bounds are evaluated in screen coordinates.
 * Compose targets register via [ComposeHeadPointerTarget] and [com.willowtree.vocable.headpointer.LocalHeadPointerRegistry].
 */
interface HeadPointerTarget {
    val listener: PointerListener
    fun containsPointer(pointerCenterScreenX: Int, pointerCenterScreenY: Int): Boolean
    val isActive: Boolean
}

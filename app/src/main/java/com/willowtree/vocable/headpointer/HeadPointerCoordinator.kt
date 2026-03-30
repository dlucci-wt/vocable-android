package com.willowtree.vocable.headpointer

import com.willowtree.vocable.customviews.PointerListener

/**
 * Resolves which [PointerListener] is under the head pointer and dispatches [PointerListener.onPointerEnter] /
 * [PointerListener.onPointerExit] when the hovered target changes.
 */
class HeadPointerCoordinator {

    private var currentListener: PointerListener? = null

    /**
     * @param targets Order matches legacy behavior: first matching active target wins.
     */
    fun onPointerAt(
        pointerCenterScreenX: Int,
        pointerCenterScreenY: Int,
        targets: List<HeadPointerTarget>,
        paused: Boolean = false,
    ) {
        if (paused) return

        val hit = targets.firstOrNull { target ->
            target.isActive && target.containsPointer(pointerCenterScreenX, pointerCenterScreenY)
        }?.listener

        if (hit !== currentListener) {
            currentListener?.onPointerExit()
            currentListener = hit
            hit?.onPointerEnter()
        }
    }

    fun clearCurrentTarget() {
        currentListener?.onPointerExit()
        currentListener = null
    }
}

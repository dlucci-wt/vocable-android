package com.willowtree.vocable.headpointer

import java.util.concurrent.CopyOnWriteArrayList

/**
 * Compose-registered [HeadPointerTarget]s. Snapshotted in display order (registration order;
 * register front-most / higher-priority targets first so [HeadPointerCoordinator] first-hit wins).
 */
class HeadPointerTargetRegistry {

    private val composeTargets = CopyOnWriteArrayList<ComposeHeadPointerTarget>()

    fun register(target: ComposeHeadPointerTarget) {
        composeTargets.addIfAbsent(target)
    }

    fun unregister(target: ComposeHeadPointerTarget) {
        composeTargets.remove(target)
    }

    fun snapshot(): List<HeadPointerTarget> = ArrayList(composeTargets)
}

package com.willowtree.vocable.headpointer

import com.willowtree.vocable.customviews.PointerListener
import org.junit.Assert.assertEquals
import org.junit.Test

/** JVM-friendly hit box (avoids android.graphics.Rect stubs in unit tests). */
private class BoxTarget(
    private val left: Int,
    private val top: Int,
    private val right: Int,
    private val bottom: Int,
    override val listener: PointerListener,
    override val isActive: Boolean = true,
) : HeadPointerTarget {
    override fun containsPointer(pointerCenterScreenX: Int, pointerCenterScreenY: Int): Boolean =
        pointerCenterScreenX in left until right && pointerCenterScreenY in top until bottom
}

class HeadPointerCoordinatorTest {

    private class RecordingListener : PointerListener {
        var enters = 0
        var exits = 0
        override fun onPointerEnter() {
            enters++
        }
        override fun onPointerExit() {
            exits++
        }
    }

    @Test
    fun firstHoverCallsEnterOnce() {
        val a = RecordingListener()
        val targets = listOf(BoxTarget(0, 0, 100, 100, a))
        val c = HeadPointerCoordinator()
        c.onPointerAt(50, 50, targets)
        assertEquals(1, a.enters)
        assertEquals(0, a.exits)
    }

    @Test
    fun stayingOnSameTargetDoesNotCallEnterAgain() {
        val a = RecordingListener()
        val targets = listOf(BoxTarget(0, 0, 100, 100, a))
        val c = HeadPointerCoordinator()
        c.onPointerAt(50, 50, targets)
        c.onPointerAt(60, 60, targets)
        assertEquals(1, a.enters)
        assertEquals(0, a.exits)
    }

    @Test
    fun movingToAnotherTargetExitsThenEnters() {
        val a = RecordingListener()
        val b = RecordingListener()
        val targets = listOf(
            BoxTarget(0, 0, 50, 50, a),
            BoxTarget(100, 100, 200, 200, b),
        )
        val c = HeadPointerCoordinator()
        c.onPointerAt(25, 25, targets)
        c.onPointerAt(150, 150, targets)
        assertEquals(1, a.enters)
        assertEquals(1, a.exits)
        assertEquals(1, b.enters)
        assertEquals(0, b.exits)
    }

    @Test
    fun clearCurrentTargetCallsExit() {
        val a = RecordingListener()
        val c = HeadPointerCoordinator()
        c.onPointerAt(5, 5, listOf(BoxTarget(0, 0, 10, 10, a)))
        c.clearCurrentTarget()
        assertEquals(1, a.exits)
    }

    @Test
    fun pausedSkipsUpdates() {
        val a = RecordingListener()
        val c = HeadPointerCoordinator()
        c.onPointerAt(5, 5, listOf(BoxTarget(0, 0, 10, 10, a)), paused = true)
        assertEquals(0, a.enters)
    }

    @Test
    fun inactiveTargetIsSkippedForFirstMatch() {
        val a = RecordingListener()
        val b = RecordingListener()
        val targets = listOf(
            object : HeadPointerTarget {
                override val listener = a
                override val isActive = false
                override fun containsPointer(pointerCenterScreenX: Int, pointerCenterScreenY: Int) = true
            },
            BoxTarget(0, 0, 100, 100, b),
        )
        val c = HeadPointerCoordinator()
        c.onPointerAt(50, 50, targets)
        assertEquals(0, a.enters)
        assertEquals(1, b.enters)
    }
}

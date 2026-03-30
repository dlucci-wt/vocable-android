package com.willowtree.vocable.utils

import android.app.ActivityManager
import android.content.Context
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.Surface
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.ArCoreApk
import com.willowtree.vocable.BuildConfig
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Interface for updating the AR pointer actions
 */
interface FaceTrackingPointerUpdates {
    fun toggleVisibility(visible: Boolean)
}

class FaceTrackingManager(
    private val activity: AppCompatActivity,
    private val faceTrackingPermissions: IFaceTrackingPermissions,
) {

    companion object {
        private const val minOpenGlVersion = 3.0
    }

    val displayMetrics = DisplayMetrics()

    /**
     * Invoked when the AR scene view should be recreated (e.g. on 180° device rotation).
     * Set this before calling [initialize].
     */
    var onArResetNeeded: (() -> Unit)? = null

    private lateinit var faceTrackingPointerUpdates: FaceTrackingPointerUpdates

    /**
     * Initializes the FaceTrackingManager and begins listening to [IFaceTrackingPermissions.PermissionState] updates.
     * @param faceTrackingPointerUpdates The interface for updating user facing AR UI elements
     */
    suspend fun initialize(faceTrackingPointerUpdates: FaceTrackingPointerUpdates) {

        this.faceTrackingPointerUpdates = faceTrackingPointerUpdates

        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        if (BuildConfig.USE_HEAD_TRACKING && checkIsSupportedDevice()) {
            coroutineScope {
                launch {
                    faceTrackingPermissions.permissionState.collect { headTrackingState ->
                        when (headTrackingState) {
                            IFaceTrackingPermissions.PermissionState.Enabled -> {
                                togglePointerVisible(true)
                                setupArTracking()
                            }

                            IFaceTrackingPermissions.PermissionState.Disabled -> {
                                togglePointerVisible(false)
                            }
                        }
                    }
                }
            }
        } else {
            // We are not actually observing in this scenario, but this will clear the permissions and still update the view
            faceTrackingPermissions.disableFaceTracking()
            togglePointerVisible(false)
        }

    }

    private var hasSetupAr: Boolean = false
    private fun setupArTracking() {
        if (!hasSetupAr) {
            hasSetupAr = true
            listenToOrientationChanges()
        }
    }

    private fun togglePointerVisible(visible: Boolean) {
        faceTrackingPointerUpdates.toggleVisibility(if (!BuildConfig.USE_HEAD_TRACKING) false else visible)
    }

    /**
     * Returns false and displays an error message if AR head tracking cannot run on this device.
     *
     * Requires ARCore and OpenGL ES 3.0+.
     *
     *
     * Disables Permissions if the device is not supported.
     */
    private fun checkIsSupportedDevice(): Boolean {
        if (ArCoreApk.getInstance().checkAvailability(activity) === ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Timber.e("TAG", "Augmented Faces requires ARCore.")
            Toast.makeText(activity, "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show()
            return false
        }
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < minOpenGlVersion) {
            Timber.e("TAG", "Head tracking requires OpenGL ES 3.0 or later")
            Toast.makeText(activity, "Head tracking requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    /**
     * Listens for device rotation and resets the AR view on 180° rotations.
     */
    private fun listenToOrientationChanges() {
        val windowManager = activity.windowManager
        val displayListener = object : DisplayManager.DisplayListener {

            private var orientation = activity.windowManager.defaultDisplay.rotation

            override fun onDisplayChanged(displayId: Int) {
                val newOrientation = windowManager.defaultDisplay.rotation
                // Only reset the AR view if device is rotated 180 degrees
                when (orientation) {
                    Surface.ROTATION_0 -> {
                        if (newOrientation == Surface.ROTATION_180) {
                            resetArView()
                        }
                    }

                    Surface.ROTATION_90 -> {
                        if (newOrientation == Surface.ROTATION_270) {
                            resetArView()
                        }
                    }

                    Surface.ROTATION_180 -> {
                        if (newOrientation == Surface.ROTATION_0) {
                            resetArView()
                        }
                    }

                    Surface.ROTATION_270 -> {
                        if (newOrientation == Surface.ROTATION_90) {
                            resetArView()
                        }
                    }
                }
                orientation = newOrientation
            }

            override fun onDisplayAdded(displayId: Int) = Unit

            override fun onDisplayRemoved(displayId: Int) = Unit
        }
        val displayManager = activity.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)
    }

    /**
     * If the device rotates 180 degrees (portrait to portrait/landscape to landscape), the
     * activity won't be destroyed and recreated. The AR scene view must be recreated to reset
     * its camera positioning. [onArResetNeeded] is invoked to signal the UI to recreate it.
     */
    private fun resetArView() {
        onArResetNeeded?.invoke()
    }
}
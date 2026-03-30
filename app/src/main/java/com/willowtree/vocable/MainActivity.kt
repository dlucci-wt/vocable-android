package com.willowtree.vocable

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.lifecycleScope
import com.willowtree.vocable.facetracking.FaceTrackView
import com.willowtree.vocable.facetracking.FaceTrackingViewModel
import com.willowtree.vocable.headpointer.HeadPointerCoordinator
import com.willowtree.vocable.headpointer.HeadPointerTargetRegistry
import com.willowtree.vocable.headpointer.LocalHeadPointerRegistry
import com.willowtree.vocable.R
import com.willowtree.vocable.ui.main.MainActivityScaffold
import com.willowtree.vocable.ui.theme.VocableTheme
import com.willowtree.vocable.utils.FaceTrackingManager
import com.willowtree.vocable.utils.FaceTrackingPointerUpdates
import com.willowtree.vocable.utils.IVocableSharedPreferences
import com.willowtree.vocable.utils.VocableEnvironment
import com.willowtree.vocable.utils.VocableEnvironmentType
import com.willowtree.vocable.utils.VocableTextToSpeech
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.getViewModel
import androidx.navigation.NavHostController

class MainActivity : ScopeActivity() {

    lateinit var vocableNavController: NavHostController
        internal set

    val headPointerRegistry = HeadPointerTargetRegistry()

    private var pointerScreenPosition by mutableStateOf(Offset.Zero)
    private var arUiWantsPointerVisible by mutableStateOf(false)
    private var pointerLayerVisible by mutableStateOf(false)
    private var faceErrorBannerVisible by mutableStateOf(false)
    private var faceTrackResetKey by mutableStateOf(0)
    private var lastShowError: Boolean = false

    private val headPointerCoordinator: HeadPointerCoordinator by inject()
    private val sharedPrefs: IVocableSharedPreferences by inject()

    private val faceTrackingManager: FaceTrackingManager by inject()
    private val environment: VocableEnvironment by inject()

    private lateinit var faceTrackingViewModel: FaceTrackingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        faceTrackingViewModel = getViewModel()

        faceTrackingManager.onArResetNeeded = { faceTrackResetKey++ }

        setContent {
            CompositionLocalProvider(LocalHeadPointerRegistry provides headPointerRegistry) {
                VocableTheme {
                    MainActivityScaffold(
                        pointerOffset = pointerScreenPosition,
                        pointerVisible = pointerLayerVisible,
                        faceErrorVisible = faceErrorBannerVisible,
                        onNavControllerReady = { vocableNavController = it },
                        faceTrackContent = {
                            key(faceTrackResetKey) {
                                FaceTrackView(viewModel = faceTrackingViewModel)
                            }
                        },
                    )
                }
            }
        }

        if (environment.environmentType != VocableEnvironmentType.TESTING) {
            lifecycleScope.launch {
                faceTrackingManager.initialize(
                    faceTrackingPointerUpdates = object : FaceTrackingPointerUpdates {
                        override fun toggleVisibility(visible: Boolean) {
                            arUiWantsPointerVisible = visible
                            applyPointerAndErrorUi()
                        }
                    },
                )
            }
        }

        faceTrackingViewModel.showError.observe(this) { show ->
            lastShowError = show == true
            if (lastShowError) {
                headPointerCoordinator.clearCurrentTarget()
            }
            applyPointerAndErrorUi()
        }

        faceTrackingViewModel.pointerLocation.observe(this) { vec ->
            vec?.let { runHeadPointerUpdate(it.x, it.y) }
        }

        supportActionBar?.hide()
        VocableTextToSpeech.initialize(this)
        applyPointerAndErrorUi()
    }

    private fun applyPointerAndErrorUi() {
        if (!sharedPrefs.getHeadTrackingEnabled()) {
            pointerLayerVisible = false
            faceErrorBannerVisible = false
            return
        }
        faceErrorBannerVisible = lastShowError
        pointerLayerVisible = !lastShowError && arUiWantsPointerVisible
    }

    override fun onDestroy() {
        headPointerCoordinator.clearCurrentTarget()
        super.onDestroy()
        VocableTextToSpeech.shutdown()
    }

    private fun runHeadPointerUpdate(x: Float, y: Float) {
        val dm = faceTrackingManager.displayMetrics
        val nx = x.coerceIn(0f, dm.widthPixels.toFloat())
        val ny = y.coerceIn(0f, dm.heightPixels.toFloat())
        pointerScreenPosition = Offset(nx, ny)

        val pointerSizePx = resources.getDimensionPixelSize(R.dimen.pointer_view_width_height)
        val pointerCenterX = nx.toInt() + pointerSizePx / 2
        val pointerCenterY = ny.toInt() + pointerSizePx / 2

        val targets = headPointerRegistry.snapshot()
        headPointerCoordinator.onPointerAt(pointerCenterX, pointerCenterY, targets, false)
    }
}

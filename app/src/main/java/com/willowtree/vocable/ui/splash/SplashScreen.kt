package com.willowtree.vocable.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Splash uses [android:windowBackground] from [com.willowtree.vocable.R.style.SplashTheme] (drawable).
 * Compose content is fully transparent so the themed window background remains visible.
 */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        // Intentionally empty — splash art comes from the window theme.
    }
}

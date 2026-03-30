package com.willowtree.vocable.ui.compose

import android.view.ContextThemeWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.willowtree.vocable.R

@Composable
fun rememberSettingsThemeContext(): ContextThemeWrapper {
    val context = LocalContext.current
    return remember(context) { ContextThemeWrapper(context, R.style.SettingsTheme) }
}

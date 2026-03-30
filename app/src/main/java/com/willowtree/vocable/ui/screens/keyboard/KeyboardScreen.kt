package com.willowtree.vocable.ui.screens.keyboard

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.willowtree.vocable.ui.keyboard.MainKeyboardContent

@Composable
fun KeyboardScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    MainKeyboardContent(
        navController = navController,
        modifier = modifier,
    )
}

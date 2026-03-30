package com.willowtree.vocable.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.willowtree.vocable.ui.screens.keyboard.AddPhraseKeyboardScreen
import com.willowtree.vocable.ui.screens.keyboard.EditCategoriesKeyboardScreen
import com.willowtree.vocable.ui.screens.keyboard.EditPhrasesKeyboardScreen
import com.willowtree.vocable.ui.screens.keyboard.KeyboardScreen
import com.willowtree.vocable.ui.screens.presets.PresetsScreen
import com.willowtree.vocable.ui.screens.settings.EditCategoriesScreen
import com.willowtree.vocable.ui.screens.settings.EditCategoryMenuScreen
import com.willowtree.vocable.ui.screens.settings.EditCategoryPhrasesScreen
import com.willowtree.vocable.ui.screens.settings.SelectionModeScreen
import com.willowtree.vocable.ui.screens.settings.SensitivityScreen
import com.willowtree.vocable.ui.screens.settings.SettingsScreen

@Composable
fun VocableNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val classLoader = LocalContext.current.classLoader

    BackHandler(enabled = navController.previousBackStackEntry != null) {
        navController.popBackStack()
    }

    NavHost(
        navController = navController,
        startDestination = VocableNavRoutes.Presets,
        modifier = modifier,
    ) {
        composable(VocableNavRoutes.Presets) {
            PresetsScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable(VocableNavRoutes.Keyboard) {
            KeyboardScreen(navController = navController, modifier = Modifier.fillMaxSize())
        }
        composable(VocableNavRoutes.Settings) {
            SettingsScreen(navController = navController, modifier = Modifier.fillMaxSize())
        }
        composable(VocableNavRoutes.Sensitivity) {
            SensitivityScreen(
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable(VocableNavRoutes.SelectionMode) {
            SelectionModeScreen(
                onBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable(VocableNavRoutes.EditCategories) {
            EditCategoriesScreen(
                navController = navController,
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable(VocableNavRoutes.EditCategoriesKeyboard) {
            EditCategoriesKeyboardScreen(
                category = null,
                navController = navController,
                modifier = Modifier.fillMaxSize(),
            )
        }
        composable(
            route = "${VocableNavRoutes.EditCategoriesKeyboard}/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType }),
        ) {
            val encoded = it.arguments?.getString("category")!!
            val category = VocableNavArgs.decodeCategory(encoded, classLoader)
            EditCategoriesKeyboardScreen(
                category = category,
                navController = navController,
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable(
            route = "${VocableNavRoutes.EditCategoryMenu}/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType }),
        ) {
            val encoded = it.arguments?.getString("category")!!
            val category = VocableNavArgs.decodeCategory(encoded, classLoader)
            EditCategoryMenuScreen(
                category = category,
                navController = navController,
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable(
            route = "${VocableNavRoutes.EditCategoryPhrases}/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType }),
        ) {
            val encoded = it.arguments?.getString("category")!!
            val category = VocableNavArgs.decodeCategory(encoded, classLoader)
            EditCategoryPhrasesScreen(
                category = category,
                navController = navController,
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable(
            route = "${VocableNavRoutes.EditPhrasesKeyboard}/{phrase}",
            arguments = listOf(navArgument("phrase") { type = NavType.StringType }),
        ) {
            val encoded = it.arguments?.getString("phrase")!!
            val phrase = VocableNavArgs.decodePhrase(encoded, classLoader)
            EditPhrasesKeyboardScreen(
                phrase = phrase,
                navController = navController,
                modifier = Modifier.fillMaxSize(),
            )
        }

        composable(
            route = "${VocableNavRoutes.AddPhraseKeyboard}/{category}",
            arguments = listOf(navArgument("category") { type = NavType.StringType }),
        ) {
            val encoded = it.arguments?.getString("category")!!
            val category = VocableNavArgs.decodeCategory(encoded, classLoader)
            AddPhraseKeyboardScreen(
                category = category,
                navController = navController,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

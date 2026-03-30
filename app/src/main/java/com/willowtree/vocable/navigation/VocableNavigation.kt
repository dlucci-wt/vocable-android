package com.willowtree.vocable.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.willowtree.vocable.presets.Category
import com.willowtree.vocable.presets.Phrase

/** True when [route] is exact or a parameterized child (e.g. `base/encoded`). */
fun NavDestination?.isBaseRoute(baseRoute: String): Boolean =
    this?.route == baseRoute || this?.route?.startsWith("$baseRoute/") == true

fun NavController.navigateToPresetsPopInclusive() {
    navigate(VocableNavRoutes.Presets) {
        popUpTo(VocableNavRoutes.Presets) { inclusive = true }
    }
}

fun NavController.navigateToEditCategoriesKeyboard(category: Category? = null) {
    navigate(VocableNavRoutes.editCategoriesKeyboard(category))
}

fun NavController.navigateToEditCategoryMenu(category: Category) {
    navigate(VocableNavRoutes.editCategoryMenu(category))
}

fun NavController.navigateToEditCategoryPhrases(category: Category) {
    navigate(VocableNavRoutes.editCategoryPhrases(category))
}

fun NavController.navigateToEditPhrasesKeyboard(phrase: Phrase) {
    navigate(VocableNavRoutes.editPhrasesKeyboard(phrase))
}

fun NavController.navigateToAddPhraseKeyboard(category: Category) {
    navigate(VocableNavRoutes.addPhraseKeyboard(category))
}

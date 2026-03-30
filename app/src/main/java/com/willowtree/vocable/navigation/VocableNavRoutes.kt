package com.willowtree.vocable.navigation

import android.os.Parcelable
import com.willowtree.vocable.presets.Category
import com.willowtree.vocable.presets.Phrase

/**
 * String routes for [androidx.navigation.compose.NavHost] (see [VocableNavHost]).
 */
object VocableNavRoutes {
    const val Presets = "presets"
    const val Keyboard = "keyboard"
    const val Settings = "settings"
    const val Sensitivity = "sensitivity"
    const val SelectionMode = "selection_mode"
    const val EditCategories = "edit_categories"
    const val EditCategoriesKeyboard = "edit_categories_keyboard"
    const val EditCategoryMenu = "edit_category_menu"
    const val EditCategoryPhrases = "edit_category_phrases"
    const val EditPhrasesKeyboard = "edit_phrases_keyboard"
    const val AddPhraseKeyboard = "add_phrase_keyboard"

    fun editCategoriesKeyboard(category: Category?): String =
        if (category == null) {
            EditCategoriesKeyboard
        } else {
            "$EditCategoriesKeyboard/${VocableNavArgs.encodeParcelable(category)}"
        }

    fun editCategoryMenu(category: Category): String =
        "$EditCategoryMenu/${VocableNavArgs.encodeParcelable(category)}"

    fun editCategoryPhrases(category: Category): String =
        "$EditCategoryPhrases/${VocableNavArgs.encodeParcelable(category)}"

    fun editPhrasesKeyboard(phrase: Phrase): String =
        "$EditPhrasesKeyboard/${VocableNavArgs.encodeParcelable(phrase as Parcelable)}"

    fun addPhraseKeyboard(category: Category): String =
        "$AddPhraseKeyboard/${VocableNavArgs.encodeParcelable(category)}"
}

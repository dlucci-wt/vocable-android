package com.willowtree.vocable.settings

import com.willowtree.vocable.presets.Phrase

/**
 * Mutable callbacks so [com.willowtree.vocable.ui.screens.settings.EditCategoryPhrasePage] always
 * invokes current navigation handlers without passing new lambdas on every recomposition.
 */
class EditCategoryPhraseActions {
    var onPhraseEdit: (Phrase) -> Unit = {}
    var onPhraseDeleteRequest: (Phrase) -> Unit = {}
}

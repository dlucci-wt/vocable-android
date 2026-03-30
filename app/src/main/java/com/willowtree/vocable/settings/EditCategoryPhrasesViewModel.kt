package com.willowtree.vocable.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.willowtree.vocable.IPhrasesUseCase
import com.willowtree.vocable.presets.Category
import com.willowtree.vocable.presets.Phrase
import com.willowtree.vocable.utils.ILocalizedResourceUtility

class EditCategoryPhrasesViewModel(
    private val category: Category,
    phrasesUseCase: IPhrasesUseCase,
    private val localizedResourceUtility: ILocalizedResourceUtility,
) : ViewModel() {

    val categoryPhraseList: LiveData<List<Phrase>> =
        phrasesUseCase.getPhrasesForCategoryFlow(category.categoryId).asLiveData()

    fun getCategoryName(): String = localizedResourceUtility.getTextFromCategory(category)
}

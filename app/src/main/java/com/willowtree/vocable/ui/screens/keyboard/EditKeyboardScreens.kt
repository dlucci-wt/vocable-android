package com.willowtree.vocable.ui.screens.keyboard

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Observer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.willowtree.vocable.R
import com.willowtree.vocable.presets.Category
import com.willowtree.vocable.presets.Phrase
import com.willowtree.vocable.settings.AddPhraseViewModel
import com.willowtree.vocable.settings.AddUpdateCategoryViewModel
import com.willowtree.vocable.settings.EditPhrasesViewModel
import com.willowtree.vocable.ui.keyboard.DiscardConfirmDialog
import com.willowtree.vocable.ui.keyboard.EditKeyboardContent
import com.willowtree.vocable.ui.keyboard.OkMessageDialog
import com.willowtree.vocable.utils.ILocalizedResourceUtility
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@Composable
fun AddPhraseKeyboardScreen(
    category: Category,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: AddPhraseViewModel = koinViewModel()
    val placeholder = remember {
        context.getString(R.string.keyboard_select_letters)
    }
    var showDiscard by remember { mutableStateOf(false) }
    var showDuplicate by remember { mutableStateOf(false) }
    EditKeyboardContent(
        initialText = placeholder,
        placeholder = placeholder,
        modifier = modifier,
        onBackWithCurrentText = { text ->
            if (text == placeholder) {
                navController.popBackStack()
            } else {
                showDiscard = true
            }
        },
        onSave = { text ->
            if (text.isNotBlank() && text != placeholder) {
                viewModel.addNewPhrase(text, category.categoryId)
            }
        },
    )

    DiscardConfirmDialog(
        visible = showDiscard,
        onDismiss = { showDiscard = false },
        onContinueEditing = { showDiscard = false },
        onDiscard = {
            showDiscard = false
            navController.popBackStack()
        },
    )

    OkMessageDialog(
        visible = showDuplicate,
        message = context.getString(R.string.duplicate_phrase),
        onDismiss = { showDuplicate = false },
    )

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = Observer<Boolean> { added ->
            when {
                added == true -> {
                    Toast.makeText(context, R.string.new_phrase_saved, Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
                added == false -> showDuplicate = true
            }
        }
        viewModel.showPhraseAdded.observe(lifecycleOwner, observer)
        onDispose { viewModel.showPhraseAdded.removeObserver(observer) }
    }
}

@Composable
fun EditPhrasesKeyboardScreen(
    phrase: Phrase,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: EditPhrasesViewModel = koinViewModel()
    val localized = getKoin().get<ILocalizedResourceUtility>()
    val placeholder = remember { context.getString(R.string.keyboard_select_letters) }
    val initialText = remember(phrase.phraseId, localized) {
        val phraseText = localized.getTextFromPhrase(phrase)
        if (phraseText.isEmpty()) placeholder else phraseText
    }
    var showDiscard by remember { mutableStateOf(false) }

    EditKeyboardContent(
        initialText = initialText,
        placeholder = placeholder,
        modifier = modifier,
        onBackWithCurrentText = { text ->
            val original = localized.getTextFromPhrase(phrase)
            if (text == original || text == placeholder) {
                navController.popBackStack()
            } else {
                showDiscard = true
            }
        },
        onSave = { text ->
            if (text.isNotBlank() && text != placeholder) {
                viewModel.updatePhrase(phrase.phraseId, text)
            }
        },
    )

    DiscardConfirmDialog(
        visible = showDiscard,
        onDismiss = { showDiscard = false },
        onContinueEditing = { showDiscard = false },
        onDiscard = {
            showDiscard = false
            navController.popBackStack()
        },
    )

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = Observer<Boolean> { saved ->
            if (saved == true) {
                Toast.makeText(context, R.string.changes_saved, Toast.LENGTH_SHORT).show()
                viewModel.phraseToFalse()
                navController.popBackStack()
            }
        }
        viewModel.showPhraseAdded.observe(lifecycleOwner, observer)
        onDispose { viewModel.showPhraseAdded.removeObserver(observer) }
    }
}

@Composable
fun EditCategoriesKeyboardScreen(
    category: Category?,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: AddUpdateCategoryViewModel = koinViewModel()
    val localized = getKoin().get<ILocalizedResourceUtility>()
    val placeholder = remember { context.getString(R.string.keyboard_select_letters) }
    val initialText = remember(category?.categoryId, localized) {
        val t = localized.getTextFromCategory(category)
        if (t.isEmpty()) placeholder else t
    }
    var showDiscard by remember { mutableStateOf(false) }
    var showDuplicateCategory by remember { mutableStateOf(false) }
    val baselineForBack = remember(category?.categoryId, localized) {
        val t = localized.getTextFromCategory(category)
        if (t.isEmpty()) placeholder else t
    }

    EditKeyboardContent(
        initialText = initialText,
        placeholder = placeholder,
        modifier = modifier,
        onBackWithCurrentText = { text ->
            if (text == baselineForBack || text == placeholder) {
                navController.popBackStack()
            } else {
                showDiscard = true
            }
        },
        onSave = { text ->
            if (text.isNotBlank() && text != placeholder) {
                if (category == null) {
                    viewModel.addCategory(text)
                } else {
                    viewModel.updateCategory(category.categoryId, text)
                }
            }
        },
    )

    DiscardConfirmDialog(
        visible = showDiscard,
        onDismiss = { showDiscard = false },
        onContinueEditing = { showDiscard = false },
        onDiscard = {
            showDiscard = false
            navController.popBackStack()
        },
    )

    OkMessageDialog(
        visible = showDuplicateCategory,
        message = context.getString(R.string.duplicate_category),
        onDismiss = { showDuplicateCategory = false },
    )

    DisposableEffect(lifecycleOwner, viewModel, category?.categoryId) {
        val updateObserver = Observer<Boolean> { show ->
            if (show == true) {
                if (category != null) {
                    Toast.makeText(context, R.string.changes_saved, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, R.string.category_created, Toast.LENGTH_SHORT).show()
                }
                navController.popBackStack()
            }
        }
        val duplicateObserver = Observer<Boolean> { show ->
            if (show == true) showDuplicateCategory = true
        }
        viewModel.showCategoryUpdateMessage.observe(lifecycleOwner, updateObserver)
        viewModel.showDuplicateCategoryMessage.observe(lifecycleOwner, duplicateObserver)
        onDispose {
            viewModel.showCategoryUpdateMessage.removeObserver(updateObserver)
            viewModel.showDuplicateCategoryMessage.removeObserver(duplicateObserver)
        }
    }
}

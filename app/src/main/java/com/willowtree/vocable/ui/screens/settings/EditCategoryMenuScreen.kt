package com.willowtree.vocable.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavHostController
import com.willowtree.vocable.R
import com.willowtree.vocable.navigation.VocableNavRoutes
import com.willowtree.vocable.navigation.isBaseRoute
import com.willowtree.vocable.navigation.navigateToEditCategoriesKeyboard
import com.willowtree.vocable.navigation.navigateToEditCategoryPhrases
import com.willowtree.vocable.presets.Category
import com.willowtree.vocable.presets.PresetCategories
import com.willowtree.vocable.settings.EditCategoryMenuViewModel
import com.willowtree.vocable.ui.compose.SettingsDialogTextButton
import com.willowtree.vocable.ui.compose.SettingsDwellSwitchRow
import com.willowtree.vocable.ui.compose.SettingsHeadImageButton
import com.willowtree.vocable.ui.compose.SettingsHeadTextBarButton
import com.willowtree.vocable.utils.ILocalizedResourceUtility
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@Composable
fun EditCategoryMenuScreen(
    category: Category,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val viewModel: EditCategoryMenuViewModel = koinViewModel()
    val localized = getKoin().get<ILocalizedResourceUtility>()

    var deleteDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(category.categoryId) {
        viewModel.updateCategoryById(category.categoryId)
    }

    val currentCategory by viewModel.currentCategory.observeAsState()
    val displayCategory = currentCategory ?: category

    val lastRemaining by viewModel.lastCategoryRemaining.observeAsState()
    val removeEnabled = lastRemaining == false && !deleteDialogVisible

    LaunchedEffect(Unit) {
        viewModel.popBackStack.collect { if (it) navController.popBackStack() }
    }

    val titleText = localized.getTextFromCategory(displayCategory)
    val recentsOrKeypad =
        displayCategory.categoryId == PresetCategories.RECENTS.id ||
            displayCategory.categoryId == PresetCategories.USER_KEYPAD.id

    val categoryVisible = !displayCategory.hidden

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorPrimaryDark))
            .padding(horizontal = dimensionResource(R.dimen.settings_screen_margin)),
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsHeadImageButton(
                    iconRes = R.drawable.category_back_button_icon,
                    width = dimensionResource(R.dimen.settings_close_button_width),
                    height = dimensionResource(R.dimen.settings_close_button_height),
                    onClick = { navController.popBackStack() },
                    enabled = !deleteDialogVisible,
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.settings_close_button_start_margin),
                        top = dimensionResource(R.dimen.settings_close_button_top_margin),
                    ),
                )
                Text(
                    text = titleText,
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = 40.dp,
                            top = dimensionResource(R.dimen.edit_category_title_margin_top),
                            end = dimensionResource(R.dimen.edit_category_pager_margin_end),
                        ),
                    textAlign = TextAlign.Start,
                    color = colorResource(R.color.textColor),
                )
            }

            Spacer(Modifier.height(50.dp))

            SettingsHeadTextBarButton(
                text = stringResource(R.string.rename_category),
                onClick = {
                    if (navController.currentDestination.isBaseRoute(VocableNavRoutes.EditCategoryMenu)) {
                        navController.navigateToEditCategoriesKeyboard(displayCategory)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 88.dp),
            )

            Spacer(Modifier.height(10.dp))

            SettingsDwellSwitchRow(
                label = stringResource(R.string.edit_category_show_category_text),
                checked = categoryVisible,
                onDwellToggle = {
                    viewModel.updateCategoryShown(displayCategory.hidden)
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(50.dp))

            SettingsHeadTextBarButton(
                text = stringResource(R.string.edit_phrases),
                onClick = {
                    if (navController.currentDestination.isBaseRoute(VocableNavRoutes.EditCategoryMenu)) {
                        val cat = currentCategory ?: category
                        navController.navigateToEditCategoryPhrases(cat)
                    }
                },
                enabled = !recentsOrKeypad,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 88.dp),
            )

            Spacer(Modifier.height(50.dp))

            SettingsHeadTextBarButton(
                text = stringResource(R.string.remove_category),
                onClick = { deleteDialogVisible = true },
                enabled = removeEnabled,
                leadingIconRes = R.drawable.ic_delete,
                trailingIconRes = null,
                backgroundRes = R.drawable.button_default_background_remove_category,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 88.dp),
            )
        }

        if (deleteDialogVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(0.9f),
                    shadowElevation = 8.dp,
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.are_you_sure),
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                        Text(
                            text = stringResource(R.string.removed_cant_be_restored),
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            SettingsDialogTextButton(
                                text = stringResource(R.string.settings_dialog_cancel),
                                onClick = { deleteDialogVisible = false },
                                modifier = Modifier.weight(1f),
                            )
                            SettingsDialogTextButton(
                                text = stringResource(R.string.delete),
                                onClick = {
                                    deleteDialogVisible = false
                                    viewModel.deleteCategory()
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

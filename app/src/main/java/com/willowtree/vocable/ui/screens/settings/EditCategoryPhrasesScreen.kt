@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.willowtree.vocable.ui.screens.settings

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.willowtree.vocable.navigation.navigateToAddPhraseKeyboard
import com.willowtree.vocable.navigation.navigateToEditPhrasesKeyboard
import com.willowtree.vocable.presets.Category
import com.willowtree.vocable.presets.Phrase
import com.willowtree.vocable.presets.PresetCategories
import com.willowtree.vocable.settings.EditCategoryPhraseActions
import com.willowtree.vocable.settings.EditCategoryPhrasesViewModel
import com.willowtree.vocable.settings.customcategories.CustomCategoryPhraseViewModel
import com.willowtree.vocable.ui.compose.SettingsDialogTextButton
import com.willowtree.vocable.ui.compose.SettingsHeadImageButton
import com.willowtree.vocable.ui.compose.SettingsHeadTextBarButton
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.min
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditCategoryPhrasesScreen(
    category: Category,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val viewModel: EditCategoryPhrasesViewModel = koinViewModel(parameters = { parametersOf(category) })
    val deletePhraseViewModel: CustomCategoryPhraseViewModel = koinViewModel(viewModelStoreOwner = activity)

    val phrasesList by viewModel.categoryPhraseList.observeAsState()
    val phrases = phrasesList.orEmpty()

    val maxPhrases = remember(context) {
        val res = context.resources
        res.getInteger(R.integer.custom_category_phrase_columns) *
            res.getInteger(R.integer.custom_category_phrase_rows)
    }

    var phraseToDelete by remember { mutableStateOf<Phrase?>(null) }
    val phraseActions = remember { EditCategoryPhraseActions() }
    SideEffect {
        phraseActions.onPhraseEdit = { phrase ->
            if (navController.currentDestination.isBaseRoute(VocableNavRoutes.EditCategoryPhrases)) {
                navController.navigateToEditPhrasesKeyboard(phrase)
            }
        }
        phraseActions.onPhraseDeleteRequest = { phraseToDelete = it }
    }

    val numPages = remember(phrases, maxPhrases) {
        when {
            phrases.isEmpty() -> 0
            maxPhrases >= phrases.size -> 1
            else -> ceil(phrases.size / maxPhrases.toDouble()).toInt()
        }
    }

    val pagerState = rememberPagerState(pageCount = { numPages.coerceAtLeast(1) })
    LaunchedEffect(numPages) {
        if (numPages > 0 && pagerState.currentPage >= numPages) {
            pagerState.scrollToPage(0)
        }
    }

    val pagePhrases: (Int) -> List<Phrase> = remember(phrases, maxPhrases, numPages) {
        { page ->
            if (phrases.isEmpty() || numPages == 0) {
                emptyList()
            } else {
                val p = page % numPages
                val start = p * maxPhrases
                phrases.subList(start, min(phrases.size, start + maxPhrases))
            }
        }
    }

    val scope = rememberCoroutineScope()
    val titleText = viewModel.getCategoryName()
    val showAddPhrase = category.categoryId != PresetCategories.RECENTS.id

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
                    enabled = phraseToDelete == null,
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
                if (showAddPhrase) {
                    SettingsHeadImageButton(
                        iconRes = R.drawable.add_40dp,
                        width = dimensionResource(R.dimen.settings_close_button_width),
                        height = dimensionResource(R.dimen.settings_close_button_height),
                        onClick = {
                            if (navController.currentDestination.isBaseRoute(VocableNavRoutes.EditCategoryPhrases)) {
                                navController.navigateToAddPhraseKeyboard(category)
                            }
                        },
                        enabled = phraseToDelete == null,
                        modifier = Modifier.padding(
                            top = dimensionResource(R.dimen.settings_close_button_top_margin),
                            end = dimensionResource(R.dimen.settings_close_button_start_margin),
                        ),
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            if (phrases.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.custom_category_empty),
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .padding(bottom = dimensionResource(R.dimen.phrases_margin)),
                        textAlign = TextAlign.Center,
                        color = colorResource(R.color.textColor),
                    )
                    SettingsHeadTextBarButton(
                        text = stringResource(R.string.add_phrase),
                        onClick = {
                            if (navController.currentDestination.isBaseRoute(VocableNavRoutes.EditCategoryPhrases)) {
                                navController.navigateToAddPhraseKeyboard(category)
                            }
                        },
                        enabled = phraseToDelete == null,
                        trailingIconRes = null,
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .height(64.dp),
                    )
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    userScrollEnabled = numPages > 1,
                ) { page ->
                    EditCategoryPhrasePage(
                        phrases = pagePhrases(page),
                        phraseActions = phraseActions,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = dimensionResource(R.dimen.edit_paging_button_bottom_margin),
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val pagingEnabled = numPages > 1 && phraseToDelete == null
                    SettingsHeadImageButton(
                        iconRes = R.drawable.phrases_back_button_icon,
                        width = dimensionResource(R.dimen.edit_paging_button_width),
                        height = dimensionResource(R.dimen.edit_paging_button_height),
                        onClick = {
                            scope.launch {
                                val pos = pagerState.currentPage
                                val target = if (pos == 0) numPages - 1 else pos - 1
                                pagerState.animateScrollToPage(target)
                            }
                        },
                        enabled = pagingEnabled,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.paging_button_margin)),
                    )
                    Text(
                        text = stringResource(
                            R.string.phrases_page_number,
                            pagerState.currentPage % numPages.coerceAtLeast(1) + 1,
                            numPages.coerceAtLeast(1),
                        ),
                        color = colorResource(R.color.textColor),
                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.edit_page_number_margin)),
                    )
                    SettingsHeadImageButton(
                        iconRes = R.drawable.phrases_forward_button_icon,
                        width = dimensionResource(R.dimen.edit_paging_button_width),
                        height = dimensionResource(R.dimen.edit_paging_button_height),
                        onClick = {
                            scope.launch {
                                val pos = pagerState.currentPage
                                val target = if (pos == numPages - 1) 0 else pos + 1
                                pagerState.animateScrollToPage(target)
                            }
                        },
                        enabled = pagingEnabled,
                        modifier = Modifier.padding(end = dimensionResource(R.dimen.paging_button_margin)),
                    )
                }
            }
        }

        if (phraseToDelete != null) {
            val pending = phraseToDelete!!
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
                            text = stringResource(R.string.delete_warning),
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            SettingsDialogTextButton(
                                text = stringResource(R.string.settings_dialog_cancel),
                                onClick = { phraseToDelete = null },
                                modifier = Modifier.weight(1f),
                            )
                            SettingsDialogTextButton(
                                text = stringResource(R.string.delete),
                                onClick = {
                                    deletePhraseViewModel.deletePhraseFromCategory(pending)
                                    phraseToDelete = null
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

@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.willowtree.vocable.ui.screens.presets

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.willowtree.vocable.MainActivity
import com.willowtree.vocable.R
import com.willowtree.vocable.headpointer.pointerHeadTarget
import com.willowtree.vocable.navigation.VocableNavRoutes
import com.willowtree.vocable.navigation.navigateToAddPhraseKeyboard
import com.willowtree.vocable.presets.CategoriesPresetsPage
import com.willowtree.vocable.presets.CategoriesPresetsPager
import com.willowtree.vocable.presets.PresetCategories
import com.willowtree.vocable.presets.PresetsPhrasesPage
import com.willowtree.vocable.presets.PresetsPhrasesPageKind
import com.willowtree.vocable.presets.PresetsViewModel
import com.willowtree.vocable.presets.PhrasesPresetsPager
import com.willowtree.vocable.ui.pointer.ImageDwellPointerListener
import com.willowtree.vocable.ui.pointer.dwellMsFromPrefs
import com.willowtree.vocable.utils.SpokenText
import com.willowtree.vocable.utils.VocableSharedPreferences
import com.willowtree.vocable.utils.VocableTextToSpeech
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PresetsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as MainActivity
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val isPortraitMode = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val isTabletMode = context.resources.getBoolean(R.bool.is_tablet)
    val maxCategories = remember(context) { context.resources.getInteger(R.integer.max_categories) }

    val presetsViewModel: PresetsViewModel = koinViewModel(viewModelStoreOwner = activity)
    val categoriesPager = remember(maxCategories) { CategoriesPresetsPager(maxItemsPerPage = maxCategories) }
    val phrasesPager = remember { PhrasesPresetsPager() }
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()

    val categoryPagerState = rememberPagerState(pageCount = { categoriesPager.numPages.coerceAtLeast(1) })
    val phrasePagerState = rememberPagerState(pageCount = { phrasesPager.numPages.coerceAtLeast(1) })

    val spokenText by SpokenText.observeAsState()
    val isSpeaking by VocableTextToSpeech.isSpeaking.observeAsState(initial = false)
    val categories by presetsViewModel.categoryList.observeAsState(initial = emptyList())
    val phrases by presetsViewModel.currentPhrases.observeAsState(initial = emptyList())
    val selectedCategoryLive by presetsViewModel.selectedCategoryLiveData.observeAsState()
    val selectedCategory by presetsViewModel.selectedCategory.collectAsStateWithLifecycle(initialValue = null)

    val isRecents = selectedCategoryLive?.categoryId == PresetCategories.RECENTS.id

    LaunchedEffect(Unit) {
        SpokenText.postValue(null)
    }

    DisposableEffect(lifecycleOwner, navController) {
        val observer = Observer<Boolean> { go ->
            if (go) {
                presetsViewModel.selectedCategory.value?.let { category ->
                    navController.navigateToAddPhraseKeyboard(category)
                }
            }
        }
        presetsViewModel.navToAddPhrase.observe(lifecycleOwner, observer)
        onDispose {
            presetsViewModel.navToAddPhrase.removeObserver(observer)
        }
    }

    LaunchedEffect(categories) {
        categoriesPager.setItems(categories)
        if (categories.isNotEmpty() && categoriesPager.numPages > 0) {
            val target = categoryPagerState.currentPage.coerceIn(0, categoriesPager.numPages - 1)
            categoryPagerState.scrollToPage(target)
        }
    }

    LaunchedEffect(selectedCategoryLive, categoriesPager.numPages) {
        val sel = selectedCategoryLive ?: return@LaunchedEffect
        if (categoriesPager.numPages <= 0) return@LaunchedEffect
        for (i in 0 until categoriesPager.numPages) {
            if (categoriesPager.getItemsByPosition(i).any { it.categoryId == sel.categoryId }) {
                categoryPagerState.scrollToPage(i)
                break
            }
        }
    }

    LaunchedEffect(selectedCategory?.categoryId) {
        if (phrasesPager.numPages > 0) {
            phrasePagerState.scrollToPage(0)
        }
    }

    LaunchedEffect(phrases, selectedCategory, isRecents) {
        val maxPhrases =
            if (selectedCategory?.categoryId == PresetCategories.USER_KEYPAD.id) {
                PhrasesPresetsPager.MAX_NUMBER_PAD_PHRASES
            } else {
                context.resources.getInteger(R.integer.max_phrases)
            }
        val pageKind = when {
            selectedCategory?.categoryId == PresetCategories.USER_KEYPAD.id ->
                PresetsPhrasesPageKind.NumberPad
            selectedCategory?.categoryId == PresetCategories.MY_SAYINGS.id && phrases.isEmpty() ->
                PresetsPhrasesPageKind.MySayingsEmpty
            else -> PresetsPhrasesPageKind.Standard
        }
        phrasesPager.setItems(phrases, maxPhrases, pageKind)
    }

    LaunchedEffect(categoryPagerState.currentPage) {
        activity.resetAllViews()
    }

    LaunchedEffect(phrasePagerState.currentPage) {
        activity.resetAllViews()
    }

    val categoriesExist = categories.isNotEmpty()
    val emptyPhrases = phrases.isEmpty() && !isRecents && categoriesPager.getSize() > 0

    val phrasePageLabel = remember(phrasePagerState.currentPage, phrasesPager.numPages, context) {
        val n = phrasesPager.numPages.coerceAtLeast(1)
        val pageNum = phrasePagerState.currentPage % n + 1
        context.getString(R.string.phrases_page_number, pageNum, n)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorPrimaryDark)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.main_activity_side_margin),
                    end = dimensionResource(R.dimen.main_activity_side_margin),
                    top = dimensionResource(R.dimen.main_activity_side_margin),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = spokenText?.takeIf { it.isNotBlank() }
                    ?: context.getString(R.string.select_something),
                modifier = Modifier.weight(1f),
                color = colorResource(R.color.textColor),
                fontSize = dimensionResource(R.dimen.spoken_text_view_text_size).value.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (isSpeaking == true) {
                Image(
                    painter = painterResource(R.drawable.ic_speaker),
                    contentDescription = null,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.speech_button_margin)),
                )
            }
        }

        PresetsTopActionRow(
            navController = navController,
            prefs = prefs,
            scope = scope,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.main_activity_side_margin))
                .padding(top = dimensionResource(R.dimen.main_activity_side_margin)),
        )

        if (!categoriesExist) {
            Text(
                text = stringResource(R.string.all_categories_hidden_text),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = dimensionResource(R.dimen.main_activity_side_margin)),
                color = colorResource(R.color.textColor),
                textAlign = TextAlign.Center,
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(R.dimen.main_activity_side_margin),
                        top = dimensionResource(R.dimen.main_activity_category_margin),
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PagingImageButton(
                    icon = R.drawable.category_back_button_icon,
                    background = R.drawable.category_button_background,
                    width = dimensionResource(R.dimen.categories_paging_button_width),
                    height = dimensionResource(R.dimen.categories_paging_button_height),
                    onClick = {
                        scope.launch {
                            val last = categoriesPager.numPages - 1
                            val p = categoryPagerState.currentPage
                            val next = if (p == 0) last else p - 1
                            selectCategoryForPresets(
                                presetsViewModel, categoriesPager, categoryPagerState,
                                isPortraitMode, isTabletMode, next,
                            )
                        }
                    },
                    prefs = prefs,
                    scope = scope,
                )
                HorizontalPager(
                    state = categoryPagerState,
                    modifier = Modifier
                        .weight(1f)
                        .height(dimensionResource(R.dimen.category_button_height))
                        .padding(horizontal = dimensionResource(R.dimen.categories_side_margin)),
                    verticalAlignment = Alignment.CenterVertically,
                ) { page ->
                    CategoriesPresetsPage(
                        categories = categoriesPager.getItemsByPosition(page),
                        maxCategories = maxCategories,
                        isTablet = isTabletMode,
                    )
                }
                PagingImageButton(
                    icon = R.drawable.category_forward_button_icon,
                    background = R.drawable.category_button_background,
                    width = dimensionResource(R.dimen.categories_paging_button_width),
                    height = dimensionResource(R.dimen.categories_paging_button_height),
                    onClick = {
                        scope.launch {
                            val last = categoriesPager.numPages - 1
                            val p = categoryPagerState.currentPage
                            val next = if (p == last) 0 else p + 1
                            selectCategoryForPresets(
                                presetsViewModel, categoriesPager, categoryPagerState,
                                isPortraitMode, isTabletMode, next,
                            )
                        }
                    },
                    prefs = prefs,
                    scope = scope,
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.main_activity_side_margin)),
                )
            }

            if (emptyPhrases) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(
                            start = dimensionResource(R.dimen.main_activity_side_margin),
                            end = dimensionResource(R.dimen.main_activity_side_margin),
                            top = dimensionResource(R.dimen.main_activity_category_margin),
                            bottom = dimensionResource(R.dimen.phrases_margin),
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.custom_category_empty),
                        color = colorResource(R.color.textColor),
                        modifier = Modifier.fillMaxWidth(0.75f),
                        textAlign = TextAlign.Center,
                    )
                    val addListener = remember(navController, presetsViewModel, prefs) {
                        ImageDwellPointerListener(
                            backgroundScope = scope,
                            getDwellMs = { dwellMsFromPrefs(prefs) },
                            onFire = {
                                presetsViewModel.selectedCategory.value?.let { category ->
                                    navController.navigateToAddPhraseKeyboard(category)
                                }
                            },
                        )
                    }
                    Text(
                        text = stringResource(R.string.add_phrase),
                        modifier = Modifier
                            .fillMaxWidth(0.4f)
                            .padding(top = dimensionResource(R.dimen.speech_button_margin))
                            .pointerHeadTarget(addListener)
                            .paint(
                                painterResource(R.drawable.button_default_background),
                                contentScale = ContentScale.FillBounds,
                            )
                            .padding(vertical = dimensionResource(R.dimen.speech_button_padding)),
                        color = colorResource(R.color.textColor),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                HorizontalPager(
                    state = phrasePagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(
                            start = dimensionResource(R.dimen.main_activity_side_margin),
                            end = dimensionResource(R.dimen.main_activity_side_margin),
                            top = dimensionResource(R.dimen.main_activity_category_margin),
                            bottom = dimensionResource(R.dimen.phrases_margin),
                        ),
                    userScrollEnabled = phrasesPager.numPages > 1,
                ) { page ->
                    PresetsPhrasesPage(
                        kind = phrasesPager.currentPageKind(),
                        phrases = phrasesPager.getItemsByPosition(page),
                    )
                }
            }

            val phraseNavEnabled = phrasesPager.numPages > 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.phrases_paging_margin_bottom)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PagingImageButton(
                    icon = R.drawable.phrases_back_button_icon,
                    background = R.drawable.button_default_background,
                    width = dimensionResource(R.dimen.phrases_paging_button_width),
                    height = dimensionResource(R.dimen.phrases_paging_button_height),
                    enabled = phraseNavEnabled,
                    onClick = {
                        scope.launch {
                            val last = phrasesPager.numPages - 1
                            val p = phrasePagerState.currentPage
                            phrasePagerState.scrollToPage(if (p == 0) last else p - 1)
                        }
                    },
                    prefs = prefs,
                    scope = scope,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.main_activity_side_margin)),
                )
                Text(
                    text = phrasePageLabel,
                    color = colorResource(R.color.textColor),
                    fontSize = dimensionResource(R.dimen.phrases_page_number_text_size).value.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensionResource(R.dimen.phrases_page_number_margin)),
                    textAlign = TextAlign.Center,
                )
                PagingImageButton(
                    icon = R.drawable.phrases_forward_button_icon,
                    background = R.drawable.button_default_background,
                    width = dimensionResource(R.dimen.phrases_paging_button_width),
                    height = dimensionResource(R.dimen.phrases_paging_button_height),
                    enabled = phraseNavEnabled,
                    onClick = {
                        scope.launch {
                            val last = phrasesPager.numPages - 1
                            val p = phrasePagerState.currentPage
                            phrasePagerState.scrollToPage(if (p == last) 0 else p + 1)
                        }
                    },
                    prefs = prefs,
                    scope = scope,
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.main_activity_side_margin)),
                )
            }
        }
    }
}

@Composable
private fun PresetsTopActionRow(
    navController: NavHostController,
    prefs: VocableSharedPreferences,
    scope: kotlinx.coroutines.CoroutineScope,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.top_bar_button_margin)),
    ) {
        val keyboardListener = remember(navController, prefs) {
            ImageDwellPointerListener(
                backgroundScope = scope,
                getDwellMs = { dwellMsFromPrefs(prefs) },
                onFire = {
                    if (navController.currentDestination?.route == VocableNavRoutes.Presets) {
                        navController.navigate(VocableNavRoutes.Keyboard)
                    }
                },
            )
        }
        val settingsListener = remember(navController, prefs) {
            ImageDwellPointerListener(
                backgroundScope = scope,
                getDwellMs = { dwellMsFromPrefs(prefs) },
                onFire = {
                    if (navController.currentDestination?.route == VocableNavRoutes.Presets) {
                        navController.navigate(VocableNavRoutes.Settings)
                    }
                },
            )
        }
        Image(
            painter = painterResource(R.drawable.keyboard_action_button_icon),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(R.dimen.top_bar_button_height))
                .pointerHeadTarget(keyboardListener)
                .paint(
                    painterResource(R.drawable.button_default_background),
                    contentScale = ContentScale.FillBounds,
                )
                .padding(8.dp),
        )
        Image(
            painter = painterResource(R.drawable.settings_action_button_icon),
            contentDescription = null,
            modifier = Modifier
                .weight(1f)
                .height(dimensionResource(R.dimen.top_bar_button_height))
                .pointerHeadTarget(settingsListener)
                .paint(
                    painterResource(R.drawable.button_default_background),
                    contentScale = ContentScale.FillBounds,
                )
                .padding(8.dp),
        )
    }
}

@Composable
private fun PagingImageButton(
    icon: Int,
    background: Int,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    prefs: VocableSharedPreferences,
    scope: kotlinx.coroutines.CoroutineScope,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val listener = remember(icon, enabled, onClick, prefs) {
        ImageDwellPointerListener(
            backgroundScope = scope,
            getDwellMs = { dwellMsFromPrefs(prefs) },
            onFire = { if (enabled) onClick() },
        )
    }
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = modifier
            .size(width, height)
            .pointerHeadTarget(listener, enabled = enabled)
            .paint(
                painterResource(background),
                contentScale = ContentScale.FillBounds,
            )
            .padding(dimensionResource(R.dimen.speech_button_padding)),
    )
}

private suspend fun selectCategoryForPresets(
    presetsViewModel: PresetsViewModel,
    categoriesPager: CategoriesPresetsPager,
    categoryPagerState: PagerState,
    isPortraitMode: Boolean,
    isTabletMode: Boolean,
    newPosition: Int,
) {
    categoryPagerState.scrollToPage(newPosition)
    if (isPortraitMode && !isTabletMode) {
        presetsViewModel.onCategorySelected(
            categoriesPager.getCategory(newPosition).categoryId,
        )
    }
}

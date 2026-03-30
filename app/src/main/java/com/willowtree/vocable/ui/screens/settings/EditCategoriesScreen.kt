@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.willowtree.vocable.ui.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.willowtree.vocable.MainActivity
import com.willowtree.vocable.R
import com.willowtree.vocable.ui.compose.SettingsHeadImageButton
import com.willowtree.vocable.ui.compose.SettingsHeadTextBarButton
import com.willowtree.vocable.navigation.VocableNavRoutes
import com.willowtree.vocable.navigation.navigateToEditCategoriesKeyboard
import com.willowtree.vocable.navigation.navigateToEditCategoryMenu
import com.willowtree.vocable.presets.Category
import com.willowtree.vocable.settings.EditCategoriesViewModel
import com.willowtree.vocable.utils.ILocalizedResourceUtility
import kotlin.math.ceil
import kotlin.math.min
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@Composable
fun EditCategoriesScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as MainActivity
    val scope = rememberCoroutineScope()
    val viewModel: EditCategoriesViewModel = koinViewModel(viewModelStoreOwner = activity)
    val localized = getKoin().get<ILocalizedResourceUtility>()
    val maxEditCategories = remember { context.resources.getInteger(R.integer.max_edit_categories) }

    val categories by viewModel.categoryList.collectAsStateWithLifecycle(initialValue = emptyList())

    val numPages = remember(categories.size, maxEditCategories) {
        if (categories.isEmpty() || maxEditCategories >= categories.size) {
            1
        } else {
            ceil(categories.size / maxEditCategories.toDouble()).toInt()
        }
    }

    val pagerState = rememberPagerState(pageCount = { numPages })

    LaunchedEffect(Unit) {
        viewModel.refreshCategories()
    }

    LaunchedEffect(numPages) {
        if (numPages > 0 && pagerState.currentPage >= numPages) {
            pagerState.scrollToPage((numPages - 1).coerceAtLeast(0))
        }
    }

    val lastViewedIndex by viewModel.lastViewedIndex.observeAsState()
    LaunchedEffect(lastViewedIndex, numPages, maxEditCategories) {
        val idx = lastViewedIndex ?: return@LaunchedEffect
        val targetPage = idx / maxEditCategories
        if (targetPage in 0 until numPages && pagerState.currentPage != targetPage) {
            pagerState.scrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState, activity) {
        snapshotFlow { pagerState.currentPage }.collect {
            activity.resetAllViews()
        }
    }

    val actionPad = dimensionResource(R.dimen.edit_sayings_back_button_padding)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorPrimaryDark)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.category_back_button_margin_start),
                    top = dimensionResource(R.dimen.edit_categories_title_margin),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsHeadImageButton(
                iconRes = R.drawable.arrow_back_40dp,
                width = dimensionResource(R.dimen.edit_categories_action_button_width),
                height = dimensionResource(R.dimen.edit_categories_action_button_height),
                onClick = { navController.popBackStack() },
                contentPadding = actionPad,
            )
            Text(
                text = stringResource(R.string.categories_edit_title),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(R.dimen.edit_categories_title_margin_horizontal)),
                color = colorResource(R.color.textColor),
                textAlign = TextAlign.Center,
            )
            SettingsHeadImageButton(
                iconRes = R.drawable.add_40dp,
                width = dimensionResource(R.dimen.edit_categories_action_button_width),
                height = dimensionResource(R.dimen.edit_categories_action_button_height),
                onClick = {
                    if (navController.currentDestination?.route == VocableNavRoutes.EditCategories) {
                        navController.navigateToEditCategoriesKeyboard(null)
                    }
                },
                contentPadding = actionPad,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.edit_categories_add_button_margin)),
            )
        }

        val configuration = LocalConfiguration.current
        val showPresetsStyleDivider =
            configuration.smallestScreenWidthDp >= 600 &&
                configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        if (showPresetsStyleDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = dimensionResource(R.dimen.edit_category_pager_margin_end))
                    .height(2.dp)
                    .background(colorResource(R.color.colorPrimary)),
            )
        }

        val pagingEnabled = numPages > 1
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(R.dimen.edit_category_pager_margin_top),
                    end = dimensionResource(R.dimen.edit_category_pager_margin_end),
                    bottom = dimensionResource(R.dimen.edit_category_pager_margin_bottom),
                ),
            userScrollEnabled = pagingEnabled,
            verticalAlignment = Alignment.Top,
        ) { page ->
            EditCategoriesPage(
                categories = categories,
                page = page,
                maxPerPage = maxEditCategories,
                localized = localized,
                navController = navController,
                viewModel = viewModel,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimensionResource(R.dimen.edit_paging_button_bottom_margin)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsHeadImageButton(
                iconRes = R.drawable.phrases_back_button_icon,
                width = dimensionResource(R.dimen.edit_paging_button_width),
                height = dimensionResource(R.dimen.edit_paging_button_height),
                onClick = {
                    scope.launch {
                        val p = pagerState.currentPage
                        val target = if (p == 0) numPages - 1 else p - 1
                        pagerState.animateScrollToPage(target)
                    }
                },
                enabled = pagingEnabled,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.paging_button_margin)),
            )
            val pageDisplay = (pagerState.currentPage % numPages.coerceAtLeast(1)) + 1
            Text(
                text = stringResource(
                    R.string.phrases_page_number,
                    pageDisplay,
                    numPages,
                ),
                modifier = Modifier
                    .padding(horizontal = dimensionResource(R.dimen.edit_page_number_margin))
                    .heightIn(min = dimensionResource(R.dimen.edit_paging_button_height)),
                color = colorResource(R.color.textColor),
                textAlign = TextAlign.Center,
            )
            SettingsHeadImageButton(
                iconRes = R.drawable.phrases_forward_button_icon,
                width = dimensionResource(R.dimen.edit_paging_button_width),
                height = dimensionResource(R.dimen.edit_paging_button_height),
                onClick = {
                    scope.launch {
                        val p = pagerState.currentPage
                        val target = if (p == numPages - 1) 0 else p + 1
                        pagerState.animateScrollToPage(target)
                    }
                },
                enabled = pagingEnabled,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.paging_button_margin)),
            )
        }
    }
}

@Composable
private fun EditCategoriesPage(
    categories: List<Category>,
    page: Int,
    maxPerPage: Int,
    localized: ILocalizedResourceUtility,
    navController: NavHostController,
    viewModel: EditCategoriesViewModel,
) {
    val start = page * maxPerPage
    if (categories.isEmpty() || start >= categories.size) {
        Spacer(Modifier.fillMaxSize())
        return
    }
    val end = min(categories.size, start + maxPerPage)
    val pageCategories = categories.subList(start, end)
    val hiddenCategories = categories.filter { it.hidden }
    val visibleCount = categories.size - hiddenCategories.size

    val arrowW = dimensionResource(R.dimen.settings_edit_individual_category_arrow_width)
    val arrowH = dimensionResource(R.dimen.settings_edit_individual_category_arrow_height)
    val arrowTop = dimensionResource(R.dimen.settings_edit_individual_category_arrow_top)
    val arrowPad = dimensionResource(R.dimen.settings_edit_individual_category_arrow_padding)
    val arrowBetween = dimensionResource(R.dimen.settings_edit_individual_category_arrow_top)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        pageCategories.forEachIndexed { index, category ->
            val overallIndex = start + index
            CategoryEditRow(
                category = category,
                overallIndex = overallIndex,
                visibleCategoryCount = visibleCount,
                localized = localized,
                navController = navController,
                viewModel = viewModel,
                arrowW = arrowW,
                arrowH = arrowH,
                arrowTop = arrowTop,
                arrowPad = arrowPad,
                arrowBetween = arrowBetween,
            )
        }
        repeat(maxPerPage - pageCategories.size) {
            Spacer(
                Modifier
                    .height(160.dp)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CategoryEditRow(
    category: Category,
    overallIndex: Int,
    visibleCategoryCount: Int,
    localized: ILocalizedResourceUtility,
    navController: NavHostController,
    viewModel: EditCategoriesViewModel,
    arrowW: androidx.compose.ui.unit.Dp,
    arrowH: androidx.compose.ui.unit.Dp,
    arrowTop: androidx.compose.ui.unit.Dp,
    arrowPad: androidx.compose.ui.unit.Dp,
    arrowBetween: androidx.compose.ui.unit.Dp,
) {
    val name = localized.getTextFromCategory(category)
    val upEnabled = !category.hidden && overallIndex > 0
    val downEnabled = !category.hidden && overallIndex + 1 < visibleCategoryCount

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = dimensionResource(R.dimen.edit_individual_category_margin_top)),
    ) {
        SettingsHeadTextBarButton(
            text = name,
            onClick = {
                if (navController.currentDestination?.route == VocableNavRoutes.EditCategories) {
                    navController.navigateToEditCategoryMenu(category)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 88.dp)
                .padding(
                    start = dimensionResource(R.dimen.edit_individual_category_padding_start),
                    end = dimensionResource(R.dimen.edit_individual_category_padding_end),
                ),
        )
        Row(
            modifier = Modifier.padding(top = arrowTop),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsHeadImageButton(
                iconRes = R.drawable.ic_up_arrow,
                width = arrowW,
                height = arrowH,
                onClick = { viewModel.moveCategoryUp(category.categoryId) },
                enabled = upEnabled,
                contentPadding = arrowPad,
            )
            Spacer(Modifier.width(arrowBetween))
            SettingsHeadImageButton(
                iconRes = R.drawable.ic_down_arrow,
                width = arrowW,
                height = arrowH,
                onClick = { viewModel.moveCategoryDown(category.categoryId) },
                enabled = downEnabled,
                contentPadding = arrowPad,
            )
        }
    }
}

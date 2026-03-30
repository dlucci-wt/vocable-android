package com.willowtree.vocable.presets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.willowtree.vocable.R
import com.willowtree.vocable.headpointer.pointerHeadTarget
import com.willowtree.vocable.ui.pointer.CategoryDwellPointerListener
import com.willowtree.vocable.ui.pointer.dwellMsFromPrefs
import com.willowtree.vocable.utils.VocableSharedPreferences
import com.willowtree.vocable.utils.locale.LocalizedResourceUtility
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun CategoriesPresetsPage(
    categories: List<Category>,
    maxCategories: Int,
    isTablet: Boolean,
) {
    val activity = LocalContext.current as ComponentActivity
    val viewModel: PresetsViewModel = koinViewModel(viewModelStoreOwner = activity)
    val selectedCategory by viewModel.selectedCategoryLiveData.observeAsState()
    val prefs: VocableSharedPreferences = koinInject()
    val localizedResourceUtility: LocalizedResourceUtility = koinInject()
    val scope = rememberCoroutineScope()

    MaterialTheme {
        CategoriesPresetsRow(
            categories = categories,
            maxCategories = maxCategories,
            isTablet = isTablet,
            selectedCategory = selectedCategory,
            viewModel = viewModel,
            prefs = prefs,
            localizedResourceUtility = localizedResourceUtility,
            scope = scope,
        )
    }
}

@Composable
private fun CategoriesPresetsRow(
    categories: List<Category>,
    maxCategories: Int,
    isTablet: Boolean,
    selectedCategory: Category?,
    viewModel: PresetsViewModel,
    prefs: VocableSharedPreferences,
    localizedResourceUtility: LocalizedResourceUtility,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    val selectedId by rememberUpdatedState(selectedCategory?.categoryId)

    @Composable
    fun CategoryRow(modifier: Modifier = Modifier) {
        Row(modifier = modifier) {
            categories.forEachIndexed { index, category ->
                val zeroStartMargin = !isTablet && index > 0 && index + 1 == maxCategories
                val side = dimensionResource(R.dimen.category_button_side_margin)
                val startPad = if (zeroStartMargin) androidx.compose.ui.unit.Dp.Hairline else side
                val label = localizedResourceUtility.getTextFromCategory(category)
                val isSelected = category.categoryId == selectedCategory?.categoryId
                val listener = remember(category.categoryId) {
                    CategoryDwellPointerListener(
                        backgroundScope = scope,
                        getDwellMs = { dwellMsFromPrefs(prefs) },
                        isSelected = { selectedId == category.categoryId },
                        onSelect = { viewModel.onCategorySelected(category.categoryId) },
                    )
                }
                val bgRes = if (isSelected) {
                    R.drawable.button_category_highlighted_background
                } else {
                    R.drawable.radio_button_default_background
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = startPad, end = side)
                        .pointerHeadTarget(listener, enabled = true)
                        .paint(
                            painterResource(bgRes),
                            contentScale = ContentScale.FillBounds,
                        )
                        .padding(horizontal = dimensionResource(R.dimen.speech_button_padding)),
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) {
                            colorResource(R.color.colorPrimaryDark)
                        } else {
                            colorResource(R.color.textColor)
                        },
                        fontSize = dimensionResource(R.dimen.category_button_text_size).value.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            repeat((maxCategories - categories.size).coerceAtLeast(0)) {
                val side = dimensionResource(R.dimen.category_button_side_margin)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = side),
                )
            }
        }
    }

    if (isTablet) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .paint(
                    painterResource(R.drawable.category_group_background),
                    contentScale = ContentScale.FillBounds,
                ),
        ) {
            CategoryRow(Modifier.fillMaxSize())
        }
    } else {
        CategoryRow(Modifier.fillMaxSize())
    }
}

@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.willowtree.vocable.presets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import com.willowtree.vocable.R
import com.willowtree.vocable.headpointer.pointerHeadTarget
import com.willowtree.vocable.ui.pointer.ImageDwellPointerListener
import com.willowtree.vocable.ui.pointer.TextDwellPointerListener
import com.willowtree.vocable.ui.pointer.dwellMsFromPrefs
import com.willowtree.vocable.utils.VocableSharedPreferences
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.Locale

enum class PresetsPhrasesPageKind {
    Standard,
    NumberPad,
    MySayingsEmpty,
}

@Composable
fun PresetsPhrasesPage(
    kind: PresetsPhrasesPageKind,
    phrases: List<PhraseGridItem>,
) {
    MaterialTheme {
        when (kind) {
            PresetsPhrasesPageKind.MySayingsEmpty -> MySayingsEmptyPresetsContent()
            PresetsPhrasesPageKind.NumberPad -> PhraseGridPage(
                phrases = phrases,
                columnsRes = R.integer.number_pad_columns,
                rowsRes = R.integer.number_pad_rows,
                phraseClickAction = null,
                phraseAddClickAction = null,
            )
            PresetsPhrasesPageKind.Standard -> {
                val activity = LocalContext.current as ComponentActivity
                val viewModel: PresetsViewModel = koinViewModel(viewModelStoreOwner = activity)
                PhraseGridPage(
                    phrases = phrases,
                    columnsRes = R.integer.phrases_columns,
                    rowsRes = R.integer.phrases_rows,
                    phraseClickAction = { viewModel.addToRecents(it) },
                    phraseAddClickAction = { viewModel.navToAddPhrase() },
                )
            }
        }
    }
}

@Composable
private fun MySayingsEmptyPresetsContent() {
    val res = LocalContext.current.resources
    val configuration = LocalConfiguration.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                    !res.getBoolean(R.bool.is_tablet)
                ) {
                    0.dp
                } else {
                    0.dp
                },
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.my_sayings_empty_handset_port),
            color = colorResource(R.color.textColor),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.main_activity_side_margin)),
        )
    }
}

@Composable
private fun PhraseGridPage(
    phrases: List<PhraseGridItem>,
    columnsRes: Int,
    rowsRes: Int,
    phraseClickAction: ((String) -> Unit)?,
    phraseAddClickAction: (() -> Unit)?,
) {
    val context = LocalContext.current
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val columns = remember(columnsRes) { context.resources.getInteger(columnsRes) }
    val rows = remember(rowsRes) { context.resources.getInteger(rowsRes) }
    val margin = dimensionResource(R.dimen.speech_button_margin)

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val minCellHeight = remember(maxHeight, rows, margin) {
            val totalSpacing = margin * (rows - 1)
            ((maxHeight - totalSpacing) / rows).coerceAtLeast(1.dp)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(margin),
            verticalArrangement = Arrangement.spacedBy(margin),
        ) {
            itemsIndexed(phrases, key = { index, item ->
                when (item) {
                    is PhraseGridItem.Phrase -> item.phraseId
                    PhraseGridItem.AddPhrase -> "add_$index"
                }
            }) { _, item ->
                when (item) {
                    is PhraseGridItem.Phrase -> {
                        val listener = remember(item.phraseId, phraseClickAction) {
                            TextDwellPointerListener(
                                backgroundScope = scope,
                                getDwellMs = { dwellMsFromPrefs(prefs) },
                                getSpeakText = { item.text },
                                locale = Locale.getDefault(),
                                onFire = { phraseClickAction?.invoke(item.phraseId) },
                            )
                        }
                        Text(
                            text = item.text,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = minCellHeight)
                                .pointerHeadTarget(listener)
                                .paint(
                                    painterResource(R.drawable.button_default_background),
                                    contentScale = ContentScale.FillBounds,
                                )
                                .padding(horizontal = dimensionResource(R.dimen.speech_button_padding)),
                            color = colorResource(R.color.textColor),
                            fontSize = dimensionResource(R.dimen.speech_button_text_size).value.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                    PhraseGridItem.AddPhrase -> {
                        val listener = remember(phraseAddClickAction) {
                            ImageDwellPointerListener(
                                backgroundScope = scope,
                                getDwellMs = { dwellMsFromPrefs(prefs) },
                                onFire = { phraseAddClickAction?.invoke() },
                            )
                        }
                        Text(
                            text = stringResource(R.string.add_phrase_plus),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = minCellHeight)
                                .pointerHeadTarget(listener)
                                .paint(
                                    painterResource(R.drawable.button_default_background_dashed),
                                    contentScale = ContentScale.FillBounds,
                                )
                                .padding(horizontal = dimensionResource(R.dimen.speech_button_padding)),
                            color = colorResource(R.color.textColor),
                            fontSize = dimensionResource(R.dimen.speech_button_text_size).value.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

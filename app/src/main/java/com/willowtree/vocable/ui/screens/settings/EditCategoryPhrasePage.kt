package com.willowtree.vocable.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.willowtree.vocable.R
import com.willowtree.vocable.headpointer.pointerHeadTarget
import com.willowtree.vocable.presets.Phrase
import com.willowtree.vocable.settings.EditCategoryPhraseActions
import com.willowtree.vocable.ui.pointer.ImageDwellPointerListener
import com.willowtree.vocable.ui.pointer.dwellMsFromPrefs
import com.willowtree.vocable.utils.VocableSharedPreferences
import org.koin.compose.koinInject

@Composable
fun EditCategoryPhrasePage(
    phrases: List<Phrase>,
    phraseActions: EditCategoryPhraseActions,
) {
    val context = LocalContext.current
    val columns = remember(context) {
        context.resources.getInteger(R.integer.custom_category_phrase_columns)
    }
    MaterialTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(phrases, key = { it.phraseId }) { phrase ->
                EditCategoryPhraseRow(
                    phrase = phrase,
                    phraseActions = phraseActions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(R.dimen.edit_category_phrase_button_margin)),
                )
            }
        }
    }
}

@Composable
private fun EditCategoryPhraseRow(
    phrase: Phrase,
    phraseActions: EditCategoryPhraseActions,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val label = phrase.text(context)
    val deleteListener = remember(phrase.phraseId, phraseActions, prefs) {
        ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }) {
            phraseActions.onPhraseDeleteRequest(phrase)
        }
    }
    val editListener = remember(phrase.phraseId, phraseActions, prefs) {
        ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }) {
            phraseActions.onPhraseEdit(phrase)
        }
    }
    val deleteSize = dimensionResource(R.dimen.edit_sayings_add_button_height)
    Row(
        modifier = modifier.height(deleteSize),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.keyboard_delete_button_icon),
            contentDescription = null,
            modifier = Modifier
                .size(deleteSize)
                .pointerHeadTarget(deleteListener)
                .paint(
                    painterResource(R.drawable.button_default_background),
                    contentScale = ContentScale.FillBounds,
                )
                .padding(5.dp),
        )
        Spacer(Modifier.width(5.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerHeadTarget(editListener)
                .paint(
                    painterResource(R.drawable.button_default_background),
                    contentScale = ContentScale.FillBounds,
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = colorResource(R.color.vocable_button_text_color),
                fontSize = dimensionResource(R.dimen.settings_button_text_size).value.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
            )
            Image(
                painter = painterResource(R.drawable.arrow_right_32dp),
                contentDescription = null,
            )
        }
    }
}

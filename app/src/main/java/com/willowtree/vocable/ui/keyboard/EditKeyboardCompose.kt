@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.willowtree.vocable.ui.keyboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.willowtree.vocable.R
import com.willowtree.vocable.headpointer.pointerHeadTarget
import com.willowtree.vocable.ui.pointer.ImageDwellPointerListener
import com.willowtree.vocable.ui.pointer.dwellMsFromPrefs
import com.willowtree.vocable.utils.VocableSharedPreferences
import org.koin.compose.koinInject

/**
 * Edit/add phrase or category keyboard. [onBackWithCurrentText] receives the current buffer so the
 * caller can decide navigation vs discard confirmation.
 */
@Composable
fun EditKeyboardContent(
    initialText: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onBackWithCurrentText: (String) -> Unit,
    onSave: (String) -> Unit,
) {
    val context = LocalContext.current
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    var buffer by remember(initialText) { mutableStateOf(initialText) }
    LaunchedEffect(initialText) {
        buffer = initialText
    }
    val keys = remember { context.resources.getStringArray(R.array.keyboard_keys) }
    val columns = remember { context.resources.getInteger(R.integer.edit_keyboard_columns) }
    val rows = remember { context.resources.getInteger(R.integer.edit_keyboard_rows) }
    val keyMargin = dimensionResource(R.dimen.keyboard_key_margin)
    val canSave = !isKeyboardPlaceholder(buffer, placeholder)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorPrimaryDark)),
    ) {
        Text(
            text = buffer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.keyboard_side_margin),
                    end = dimensionResource(R.dimen.keyboard_side_margin),
                    top = 16.dp,
                ),
            color = colorResource(R.color.textColor),
            fontSize = dimensionResource(R.dimen.edit_keyboard_text_size).value.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.keyboard_side_margin),
                    end = dimensionResource(R.dimen.keyboard_side_margin),
                    top = dimensionResource(R.dimen.edit_keyboard_top_margin),
                ),
        ) {
            val backL = remember(prefs, buffer) {
                ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }) {
                    onBackWithCurrentText(buffer)
                }
            }
            val saveL = remember(prefs, canSave, buffer) {
                ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }) {
                    if (canSave) onSave(buffer)
                }
            }
            Image(
                painter = painterResource(R.drawable.category_back_button_icon),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .height(dimensionResource(R.dimen.edit_keyboard_button_height))
                    .padding(end = dimensionResource(R.dimen.keyboard_bottom_bar_key_margin))
                    .pointerHeadTarget(backL)
                    .paint(
                        painterResource(R.drawable.button_default_background),
                        contentScale = ContentScale.FillBounds,
                    )
                    .padding(dimensionResource(R.dimen.edit_keyboard_padding)),
            )
            Image(
                painter = painterResource(R.drawable.checkmark),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .height(dimensionResource(R.dimen.edit_keyboard_button_height))
                    .pointerHeadTarget(saveL, enabled = canSave)
                    .paint(
                        painterResource(R.drawable.button_default_background),
                        contentScale = ContentScale.FillBounds,
                    )
                    .padding(dimensionResource(R.dimen.edit_keyboard_padding)),
            )
        }

        KeyboardKeyGrid(
            keys = keys,
            columns = columns,
            rows = rows,
            keyMargin = keyMargin,
            onKey = { k -> buffer = appendKeyboardKey(buffer, k, placeholder) },
            prefs = prefs,
            scope = scope,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    start = dimensionResource(R.dimen.keyboard_side_margin),
                    end = dimensionResource(R.dimen.keyboard_side_margin),
                    top = dimensionResource(R.dimen.keyboard_keys_top_margin),
                    bottom = dimensionResource(R.dimen.edit_keyboard_bottom_margin),
                ),
        )

        KeyboardBottomRow(
            clear = { buffer = placeholder },
            space = {
                if (!isKeyboardPlaceholder(buffer, placeholder) && !buffer.endsWith(' ')) {
                    buffer += " "
                }
            },
            backspace = {
                if (!isKeyboardPlaceholder(buffer, placeholder)) {
                    buffer = buffer.dropLast(1)
                    if (buffer.isEmpty()) buffer = placeholder
                }
            },
            speak = { },
            showSpeak = false,
            spaceWeight = 3f,
            prefs = prefs,
            scope = scope,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.keyboard_side_margin),
                    end = dimensionResource(R.dimen.keyboard_side_margin),
                    bottom = dimensionResource(R.dimen.keyboard_bottom_bar_margin),
                ),
        )
    }
}

@Composable
fun DiscardConfirmDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onContinueEditing: () -> Unit,
    onDiscard: () -> Unit,
) {
    if (!visible) return
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val cancelL = remember(prefs) {
        ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }) {
            onContinueEditing()
        }
    }
    val discardL = remember(prefs) {
        ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }) {
            onDiscard()
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.are_you_sure)) },
        text = { Text(stringResource(R.string.back_warning)) },
        confirmButton = {
            Text(
                stringResource(R.string.contiue_editing),
                Modifier.pointerHeadTarget(cancelL),
            )
        },
        dismissButton = {
            Text(
                stringResource(R.string.discard),
                Modifier.pointerHeadTarget(discardL),
            )
        },
    )
}

@Composable
fun OkMessageDialog(
    visible: Boolean,
    message: String,
    onDismiss: () -> Unit,
) {
    if (!visible) return
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val ok = remember(prefs) {
        ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }, onDismiss)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { Text(message) },
        confirmButton = {
            Text(stringResource(android.R.string.ok), Modifier.pointerHeadTarget(ok))
        },
    )
}

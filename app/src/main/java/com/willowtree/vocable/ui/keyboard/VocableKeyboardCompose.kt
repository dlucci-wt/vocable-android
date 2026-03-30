@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.willowtree.vocable.ui.keyboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.willowtree.vocable.R
import com.willowtree.vocable.headpointer.pointerHeadTarget
import com.willowtree.vocable.navigation.VocableNavRoutes
import com.willowtree.vocable.navigation.navigateToPresetsPopInclusive
import com.willowtree.vocable.ui.pointer.ImageDwellPointerListener
import com.willowtree.vocable.ui.pointer.dwellMsFromPrefs
import com.willowtree.vocable.utils.VocableSharedPreferences
import com.willowtree.vocable.utils.VocableTextToSpeech
import org.koin.compose.koinInject
import java.util.Locale

internal fun appendKeyboardKey(current: String, key: String, placeholder: String): String {
    if (current == placeholder) return key
    if (current.endsWith(". ") || current.endsWith("? ")) return current + key
    return current + key.lowercase(Locale.getDefault())
}

internal fun isKeyboardPlaceholder(text: String, placeholder: String) = text == placeholder

@Composable
fun MainKeyboardContent(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val placeholder = stringResource(R.string.keyboard_select_letters)
    var buffer by remember { mutableStateOf(placeholder) }
    val keys = remember { context.resources.getStringArray(R.array.keyboard_keys) }
    val columns = remember { context.resources.getInteger(R.integer.keyboard_columns) }
    val rows = remember { context.resources.getInteger(R.integer.keyboard_rows) }
    val keyMargin = dimensionResource(R.dimen.keyboard_key_margin)
    val isSpeaking by VocableTextToSpeech.isSpeaking.observeAsState(false)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.colorPrimaryDark)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = dimensionResource(R.dimen.keyboard_side_margin)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = buffer,
                modifier = Modifier.weight(1f),
                color = colorResource(R.color.textColor),
                fontSize = dimensionResource(R.dimen.keyboard_input_text_size).value.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
            )
            if (isSpeaking == true) {
                Image(
                    painter = painterResource(R.drawable.ic_speaker),
                    contentDescription = null,
                    modifier = Modifier.padding(start = dimensionResource(R.dimen.speech_button_margin)),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = dimensionResource(R.dimen.keyboard_side_margin),
                    end = dimensionResource(R.dimen.keyboard_side_margin),
                    top = 24.dp,
                ),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.top_bar_button_margin)),
        ) {
            val presetsListener = remember(navController, prefs) {
                ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }) {
                    if (navController.currentDestination?.route == VocableNavRoutes.Keyboard) {
                        navController.navigateToPresetsPopInclusive()
                    }
                }
            }
            val settingsListener = remember(navController, prefs) {
                ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }) {
                    if (navController.currentDestination?.route == VocableNavRoutes.Keyboard) {
                        navController.navigate(VocableNavRoutes.Settings)
                    }
                }
            }
            KeyboardTopBarIcon(
                icon = R.drawable.presets_action_button_icon,
                listener = presetsListener,
                modifier = Modifier.weight(1f),
            )
            KeyboardTopBarIcon(
                icon = R.drawable.settings_action_button_icon,
                listener = settingsListener,
                modifier = Modifier.weight(1f),
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
                    bottom = dimensionResource(R.dimen.keyboard_side_margin),
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
            speak = {
                if (!isKeyboardPlaceholder(buffer, placeholder)) {
                    VocableTextToSpeech.speak(Locale.getDefault(), buffer)
                }
            },
            showSpeak = true,
            spaceWeight = 2f,
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
private fun KeyboardTopBarIcon(
    icon: Int,
    listener: ImageDwellPointerListener,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = modifier
            .height(dimensionResource(R.dimen.top_bar_button_height))
            .pointerHeadTarget(listener)
            .paint(
                painterResource(R.drawable.button_default_background),
                contentScale = ContentScale.FillBounds,
            )
            .padding(8.dp),
    )
}

@Composable
fun KeyboardKeyGrid(
    keys: Array<String>,
    columns: Int,
    rows: Int,
    keyMargin: androidx.compose.ui.unit.Dp,
    onKey: (String) -> Unit,
    prefs: VocableSharedPreferences,
    scope: kotlinx.coroutines.CoroutineScope,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val minH = remember(maxHeight, rows, keyMargin) {
            val total = keyMargin * (rows - 1)
            ((maxHeight - total) / rows).coerceAtLeast(1.dp)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            horizontalArrangement = Arrangement.spacedBy(keyMargin),
            verticalArrangement = Arrangement.spacedBy(keyMargin),
        ) {
            items(keys.toList()) { key ->
                val listener = remember(key, onKey) {
                    ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }) { onKey(key) }
                }
                Text(
                    text = key,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = minH)
                        .pointerHeadTarget(listener)
                        .paint(
                            painterResource(R.drawable.button_default_background),
                            contentScale = ContentScale.FillBounds,
                        ),
                    color = colorResource(R.color.vocable_button_text_color),
                    fontWeight = FontWeight.Bold,
                    fontSize = dimensionResource(R.dimen.keyboard_text_size).value.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun KeyboardBottomRow(
    clear: () -> Unit,
    space: () -> Unit,
    backspace: () -> Unit,
    speak: () -> Unit,
    showSpeak: Boolean,
    spaceWeight: Float,
    prefs: VocableSharedPreferences,
    scope: kotlinx.coroutines.CoroutineScope,
    modifier: Modifier = Modifier,
) {
    val bottomH = dimensionResource(R.dimen.keyboard_bottom_bar_height)
    val keyEnd = dimensionResource(R.dimen.keyboard_bottom_bar_key_margin)
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        BottomIconKey(R.drawable.keyboard_delete_button_icon, 1f, bottomH, keyEnd, clear, prefs, scope)
        BottomIconKey(R.drawable.keyboard_space_button_icon, spaceWeight, bottomH, keyEnd, space, prefs, scope)
        BottomIconKey(R.drawable.keyboard_backspace_button_icon, 1f, bottomH, keyEnd, backspace, prefs, scope)
        if (showSpeak) {
            val speakL = remember(prefs) {
                ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }, speak)
            }
            Image(
                painter = painterResource(R.drawable.keyboard_speak_button_icon),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .height(bottomH)
                    .pointerHeadTarget(speakL)
                    .paint(
                        painterResource(R.drawable.button_speaker_background),
                        contentScale = ContentScale.FillBounds,
                    ),
            )
        }
    }
}

@Composable
private fun RowScope.BottomIconKey(
    icon: Int,
    weight: Float,
    height: androidx.compose.ui.unit.Dp,
    endMargin: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    prefs: VocableSharedPreferences,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    val listener = remember(icon, onClick) {
        ImageDwellPointerListener(scope, { dwellMsFromPrefs(prefs) }, onClick)
    }
    Image(
        painter = painterResource(icon),
        contentDescription = null,
        modifier = Modifier
            .weight(weight)
            .height(height)
            .padding(end = endMargin)
            .pointerHeadTarget(listener)
            .paint(
                painterResource(R.drawable.button_default_background),
                contentScale = ContentScale.FillBounds,
            ),
    )
}

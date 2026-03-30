package com.willowtree.vocable.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.willowtree.vocable.R
import com.willowtree.vocable.headpointer.pointerHeadTarget
import com.willowtree.vocable.ui.pointer.CategoryDwellPointerListener
import com.willowtree.vocable.ui.pointer.ImageDwellPointerListener
import com.willowtree.vocable.ui.pointer.dwellMsFromPrefs
import com.willowtree.vocable.utils.VocableSharedPreferences
import org.koin.compose.koinInject

@Composable
fun SettingsHeadImageButton(
    iconRes: Int,
    width: Dp,
    height: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundRes: Int? = R.drawable.button_default_background,
    contentPadding: Dp = 0.dp,
) {
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val listener = remember(iconRes, enabled, onClick, prefs) {
        ImageDwellPointerListener(
            backgroundScope = scope,
            getDwellMs = { dwellMsFromPrefs(prefs) },
            onFire = { if (enabled) onClick() },
        )
    }
    val base = Modifier
        .size(width, height)
        .pointerHeadTarget(listener, enabled = enabled)
        .then(
            if (backgroundRes != null) {
                Modifier.paint(
                    painterResource(backgroundRes),
                    contentScale = ContentScale.FillBounds,
                )
            } else {
                Modifier
            },
        )
        .padding(contentPadding)
        .alpha(if (enabled) 1f else 0.4f)
    Image(
        painter = painterResource(iconRes),
        contentDescription = null,
        modifier = modifier.then(base),
    )
}

@Composable
fun SettingsHeadTextBarButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    minHeight: Dp = 0.dp,
    horizontalPadding: Dp = dimensionResource(R.dimen.settings_option_button_padding),
    verticalPadding: Dp = 8.dp,
    trailingIconRes: Int? = R.drawable.arrow_right_32dp,
    leadingIconRes: Int? = null,
    backgroundRes: Int = R.drawable.button_default_background,
    textSizeSp: Float = dimensionResource(R.dimen.settings_button_text_size).value,
) {
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val listener = remember(text, enabled, onClick, prefs) {
        ImageDwellPointerListener(
            backgroundScope = scope,
            getDwellMs = { dwellMsFromPrefs(prefs) },
            onFire = { if (enabled) onClick() },
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .pointerHeadTarget(listener, enabled = enabled)
            .paint(
                painterResource(backgroundRes),
                contentScale = ContentScale.FillBounds,
            )
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
            .alpha(if (enabled) 1f else 0.4f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        if (leadingIconRes != null) {
            Image(
                painter = painterResource(leadingIconRes),
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
            )
        }
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(end = if (trailingIconRes != null) 8.dp else 0.dp),
            color = colorResource(R.color.vocable_button_text_color),
            fontSize = textSizeSp.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Start,
        )
        if (trailingIconRes != null) {
            Image(
                painter = painterResource(trailingIconRes),
                contentDescription = null,
            )
        }
    }
}

@Composable
fun SettingsDialogTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val listener = remember(text, enabled, onClick, prefs) {
        ImageDwellPointerListener(
            backgroundScope = scope,
            getDwellMs = { dwellMsFromPrefs(prefs) },
            onFire = { if (enabled) onClick() },
        )
    }
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .pointerHeadTarget(listener, enabled = enabled)
            .paint(
                painterResource(R.drawable.vocable_dialog_button_background),
                contentScale = ContentScale.FillBounds,
            )
            .padding(
                horizontal = dimensionResource(R.dimen.dialog_button_padding_horizontal),
                vertical = dimensionResource(R.dimen.dialog_button_padding_vertical),
            )
            .alpha(if (enabled) 1f else 0.4f),
        color = colorResource(R.color.colorPrimary),
        fontSize = dimensionResource(R.dimen.dialog_button_text_size).value.sp,
        textAlign = TextAlign.Center,
        maxLines = 2,
    )
}

@Composable
fun SettingsSensitivityCategoryButton(
    label: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val listener = remember(label, selected, onSelect, prefs) {
        CategoryDwellPointerListener(
            backgroundScope = scope,
            getDwellMs = { dwellMsFromPrefs(prefs) },
            isSelected = { selected },
            onSelect = onSelect,
        )
    }
    val bgRes = if (selected) {
        R.drawable.button_category_highlighted_background
    } else {
        R.drawable.radio_button_default_background
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .pointerHeadTarget(listener, enabled = true)
            .paint(
                painterResource(bgRes),
                contentScale = ContentScale.FillBounds,
            )
            .padding(horizontal = dimensionResource(R.dimen.speech_button_padding)),
    ) {
        Text(
            text = label,
            color = if (selected) {
                colorResource(R.color.colorPrimaryDark)
            } else {
                colorResource(R.color.textColor)
            },
            fontSize = dimensionResource(R.dimen.category_button_text_size).value.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Settings row: label + read-only [Switch]; whole row uses head-pointer dwell to fire [onDwellToggle]. */
@Composable
fun SettingsDwellSwitchRow(
    label: String,
    checked: Boolean,
    onDwellToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val prefs: VocableSharedPreferences = koinInject()
    val scope = rememberCoroutineScope()
    val rowListener = remember(onDwellToggle, prefs) {
        ImageDwellPointerListener(
            backgroundScope = scope,
            getDwellMs = { dwellMsFromPrefs(prefs) },
            onFire = onDwellToggle,
        )
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = dimensionResource(R.dimen.selection_mode_button_height))
            .pointerHeadTarget(rowListener)
            .paint(
                painterResource(R.drawable.settings_group_background),
                contentScale = ContentScale.FillBounds,
            )
            .padding(
                start = dimensionResource(R.dimen.settings_option_button_padding),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = dimensionResource(R.dimen.settings_margin_default)),
            color = colorResource(R.color.textColor),
            fontSize = dimensionResource(R.dimen.edit_sayings_text_size).value.sp,
        )
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = false,
            modifier = Modifier
                .scale(1.5f)
                .padding(
                    end = dimensionResource(R.dimen.settings_margin_default),
                    top = dimensionResource(R.dimen.settings_margin_default),
                    bottom = dimensionResource(R.dimen.settings_margin_default),
                ),
        )
    }
}

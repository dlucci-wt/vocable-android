package com.willowtree.vocable.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.willowtree.vocable.R
import com.willowtree.vocable.ui.compose.SettingsHeadImageButton
import com.willowtree.vocable.ui.compose.SettingsSensitivityCategoryButton
import com.willowtree.vocable.utils.VocableSharedPreferences
import org.koin.compose.koinInject
import java.text.DecimalFormat

private const val LOW_SENSITIVITY = 0.05f
private const val MEDIUM_SENSITIVITY = 0.1f
private const val HIGH_SENSITIVITY = 0.15f
private const val DWELL_TIME_CHANGE = 500L
private const val DWELL_TIME_ONE_SECOND = 1000L
private const val MIN_DWELL_TIME = 500L
private const val MAX_DWELL_TIME = 4000L

@Composable
fun SensitivityScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sharedPrefs: VocableSharedPreferences = koinInject()
    val context = LocalContext.current

    var dwellTime by remember {
        mutableLongStateOf(sharedPrefs.getDwellTime())
    }
    var sensIndex by remember {
        mutableIntStateOf(
            when (sharedPrefs.getSensitivity()) {
                LOW_SENSITIVITY -> 0
                MEDIUM_SENSITIVITY -> 1
                else -> 2
            },
        )
    }

    val decEnabled = dwellTime > MIN_DWELL_TIME
    val incEnabled = dwellTime < MAX_DWELL_TIME

    val hoverLabelText = remember(dwellTime) {
        if (dwellTime == DWELL_TIME_ONE_SECOND) {
            context.getString(R.string.hover_time_one_text)
        } else {
            val df = DecimalFormat("#.#")
            context.getString(
                R.string.hover_time_amount_text,
                df.format(dwellTime.toDouble() / DWELL_TIME_ONE_SECOND),
            )
        }
    }

    val horizontalPad = dimensionResource(R.dimen.settings_screen_margin)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPad),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsHeadImageButton(
                iconRes = R.drawable.category_back_button_icon,
                width = dimensionResource(R.dimen.settings_close_button_width),
                height = dimensionResource(R.dimen.settings_close_button_height),
                onClick = onBack,
                modifier = Modifier.padding(top = dimensionResource(R.dimen.settings_close_button_top_margin)),
            )
            Text(
                text = stringResource(R.string.timing_sensitivity_title),
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = dimensionResource(R.dimen.settings_title_padding_start),
                        end = dimensionResource(R.dimen.settings_title_padding_end),
                    ),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.hover_time_text_top_margin)))

        Text(text = stringResource(R.string.hover_time_text), modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SettingsHeadImageButton(
                iconRes = R.drawable.decrease_40dp,
                width = dimensionResource(R.dimen.hover_time_button_width),
                height = dimensionResource(R.dimen.hover_time_button_height),
                onClick = {
                    dwellTime = (dwellTime - DWELL_TIME_CHANGE).coerceAtLeast(MIN_DWELL_TIME)
                    sharedPrefs.setDwellTime(dwellTime)
                },
                enabled = decEnabled,
            )
            Text(
                text = hoverLabelText,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )
            SettingsHeadImageButton(
                iconRes = R.drawable.add_40dp,
                width = dimensionResource(R.dimen.hover_time_button_width),
                height = dimensionResource(R.dimen.hover_time_button_height),
                onClick = {
                    dwellTime = (dwellTime + DWELL_TIME_CHANGE).coerceAtMost(MAX_DWELL_TIME)
                    sharedPrefs.setDwellTime(dwellTime)
                },
                enabled = incEnabled,
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.cursor_sensitivity_margin_top)))

        Text(
            text = stringResource(R.string.cursor_sensitivity_text),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.cursor_button_margin_top)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.medium_button_horizontal_margin),
            ),
        ) {
            SettingsSensitivityCategoryButton(
                label = stringResource(R.string.cursor_sensitivity_low),
                selected = sensIndex == 0,
                onSelect = {
                    sharedPrefs.setSensitivity(LOW_SENSITIVITY)
                    sensIndex = 0
                },
                modifier = Modifier
                    .weight(1f)
                    .height(dimensionResource(R.dimen.cursor_button_height)),
            )
            SettingsSensitivityCategoryButton(
                label = stringResource(R.string.cursor_sensitivity_medium),
                selected = sensIndex == 1,
                onSelect = {
                    sharedPrefs.setSensitivity(MEDIUM_SENSITIVITY)
                    sensIndex = 1
                },
                modifier = Modifier
                    .weight(1f)
                    .height(dimensionResource(R.dimen.cursor_button_height)),
            )
            SettingsSensitivityCategoryButton(
                label = stringResource(R.string.cursor_sensitivity_high),
                selected = sensIndex == 2,
                onSelect = {
                    sharedPrefs.setSensitivity(HIGH_SENSITIVITY)
                    sensIndex = 2
                },
                modifier = Modifier
                    .weight(1f)
                    .height(dimensionResource(R.dimen.cursor_button_height)),
            )
        }
    }
}

package com.willowtree.vocable.ui.screens.settings

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.willowtree.vocable.MainActivity
import com.willowtree.vocable.R
import com.willowtree.vocable.settings.selectionmode.SelectionModeViewModel
import com.willowtree.vocable.ui.compose.SettingsDwellSwitchRow
import com.willowtree.vocable.ui.compose.SettingsHeadImageButton
import org.koin.androidx.compose.koinViewModel

@Composable
fun SelectionModeScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalContext.current as MainActivity
    val viewModel: SelectionModeViewModel = koinViewModel(viewModelStoreOwner = activity)
    val headEnabled by viewModel.headTrackingEnabled.observeAsState(initial = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.settings_screen_margin)),
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
                text = stringResource(R.string.selection_mode_title),
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = dimensionResource(R.dimen.settings_title_padding_start),
                        end = dimensionResource(R.dimen.settings_title_padding_end),
                    ),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.height(dimensionResource(R.dimen.settings_options_margin_top)))

        SettingsDwellSwitchRow(
            label = stringResource(R.string.settings_head_tracking),
            checked = headEnabled,
            onDwellToggle = {
                if (headEnabled) {
                    viewModel.disableHeadTracking()
                } else {
                    viewModel.requestHeadTracking()
                }
            },
        )

        Spacer(Modifier.height(dimensionResource(R.dimen.settings_options_margin_bottom)))
    }
}

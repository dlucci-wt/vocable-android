package com.willowtree.vocable.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.willowtree.vocable.BuildConfig
import com.willowtree.vocable.MainActivity
import com.willowtree.vocable.R
import com.willowtree.vocable.navigation.VocableNavRoutes
import com.willowtree.vocable.ui.compose.SettingsDialogTextButton
import com.willowtree.vocable.ui.compose.SettingsHeadImageButton
import com.willowtree.vocable.ui.compose.SettingsHeadTextBarButton

private const val PRIVACY_POLICY = "https://vocable.app/privacy.html"
private const val MAIL_TO =
    "mailto:vocable@willowtreeapps.com?subject=Feedback for Android Vocable "

@Composable
fun SettingsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = context as MainActivity
    var pendingExternalAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var dialogLocked by remember { mutableStateOf(false) }

    val cols = remember { context.resources.getInteger(R.integer.settings_options_columns) }

    fun showLeavingDialog(block: () -> Unit) {
        pendingExternalAction = block
        dialogLocked = true
    }

    fun dismissDialog() {
        pendingExternalAction = null
        dialogLocked = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(R.dimen.settings_screen_margin)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SettingsHeadImageButton(
                    iconRes = R.drawable.close_action_button_icon,
                    width = dimensionResource(R.dimen.settings_close_button_width),
                    height = dimensionResource(R.dimen.settings_close_button_height),
                    onClick = { navController.popBackStack() },
                    enabled = !dialogLocked,
                    backgroundRes = null,
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.settings_close_button_top_margin)),
                )
                Text(
                    text = stringResource(R.string.settings),
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(cols),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    listOf(
                        R.string.edit_categories_title to {
                            if (navController.currentDestination?.route == VocableNavRoutes.Settings) {
                                navController.navigate(VocableNavRoutes.EditCategories)
                            }
                        },
                        R.string.timing_sensitivity_title to {
                            if (navController.currentDestination?.route == VocableNavRoutes.Settings) {
                                navController.navigate(VocableNavRoutes.Sensitivity)
                            }
                        },
                        R.string.settings_selection_mode to {
                            if (navController.currentDestination?.route == VocableNavRoutes.Settings) {
                                navController.navigate(VocableNavRoutes.SelectionMode)
                            }
                        },
                    ),
                ) { (labelRes, onClick) ->
                    SettingsHeadTextBarButton(
                        text = stringResource(labelRes),
                        onClick = onClick,
                        enabled = !dialogLocked,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 88.dp),
                    )
                }
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.settings_contact_developers_top_margin)))

            SettingsHeadTextBarButton(
                text = stringResource(R.string.privacy_policy),
                onClick = {
                    showLeavingDialog {
                        activity.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY)),
                        )
                    }
                },
                enabled = !dialogLocked,
                trailingIconRes = R.drawable.launch_32dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = dimensionResource(R.dimen.settings_link_button_height)),
            )

            SettingsHeadTextBarButton(
                text = stringResource(R.string.contact_developers),
                onClick = {
                    showLeavingDialog {
                        val sendEmail = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse(
                                "$MAIL_TO${BuildConfig.VERSION_NAME}-${BuildConfig.VERSION_CODE}",
                            )
                        }
                        activity.startActivity(sendEmail)
                    }
                },
                enabled = !dialogLocked,
                trailingIconRes = R.drawable.launch_32dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = dimensionResource(R.dimen.settings_link_button_height))
                    .padding(top = dimensionResource(R.dimen.settings_contact_developers_top_margin)),
            )

            Text(
                text = stringResource(R.string.version, BuildConfig.VERSION_NAME),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(R.dimen.settings_contact_developers_top_margin),
                        bottom = dimensionResource(R.dimen.settings_version_text_bottom_margin),
                    ),
                textAlign = TextAlign.Center,
            )
        }

        pendingExternalAction?.let { action ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(0.9f),
                    shadowElevation = 8.dp,
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.settings_dialog_title),
                            modifier = Modifier.padding(bottom = 8.dp),
                        )
                        Text(
                            text = stringResource(R.string.settings_dialog_message),
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            SettingsDialogTextButton(
                                text = stringResource(R.string.settings_dialog_cancel),
                                onClick = { dismissDialog() },
                                modifier = Modifier.weight(1f),
                            )
                            SettingsDialogTextButton(
                                text = stringResource(R.string.settings_dialog_continue),
                                onClick = {
                                    action()
                                    dismissDialog()
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

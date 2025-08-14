package com.willowtree.vocable.settings

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.willowtree.vocable.BaseFragment
import com.willowtree.vocable.BindingInflater
import com.willowtree.vocable.BuildConfig
import com.willowtree.vocable.R
import com.willowtree.vocable.composables.VocableButtonWithEndImage
import com.willowtree.vocable.composables.VocableImageButton
import com.willowtree.vocable.composables.VocableTextView
import com.willowtree.vocable.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    companion object {
        private const val PRIVACY_POLICY = "https://vocable.app/privacy.html"
        private const val MAIL_TO =
            "mailto:vocable@willowtreeapps.com?subject=Feedback for Android Vocable "
        private const val SETTINGS_OPTION_COUNT = 5
    }

    override val bindingInflater: BindingInflater<FragmentSettingsBinding> =
        FragmentSettingsBinding::inflate
    private var numColumns = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        numColumns = resources.getInteger(R.integer.settings_options_columns)

//        (binding.settingsOptionsContainer.root as GridLayout).children.forEachIndexed { index, child ->
//            if (index % numColumns == numColumns - 1) {
//                child.layoutParams = (child.layoutParams as GridLayout.LayoutParams).apply {
//                    marginEnd = 0
//                }
//            }
//            if (index > SETTINGS_OPTION_COUNT - numColumns) {
//                child.layoutParams = (child.layoutParams as GridLayout.LayoutParams).apply {
//                    updateMargins(bottom = 0)
//                }
//            }
//        }

        binding.compose?.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {

                val context = LocalContext.current
                val exitDrawable = ContextCompat.getDrawable(
                    context,
                    R.drawable.close_action_button_icon
                ) as Drawable
                val endDrawable =
                    ContextCompat.getDrawable(context, R.drawable.arrow_right_32dp) as Drawable
                val fontSize = with(LocalDensity.current) {
                    context.resources.getDimension(R.dimen.settings_title_text_size).toSp()
                }


                Column(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 48.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VocableImageButton(
                            height = 24.dp,
                            width = 24.dp,
                            backgroundColor = Color(R.drawable.button_default_background),
                            painter = BitmapPainter(exitDrawable.toBitmap().asImageBitmap()),
                            onClick = { findNavController().popBackStack() },
                        )
                        VocableTextView(
                            text = context.resources.getString(R.string.settings),
                            fontSize = fontSize,
                            fontWeight = FontWeight.Bold,
                            textColor = colorResource(R.color.textColor),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(2f)
                        )
                    }

                    VocableButtonWithEndImage(
                        onClick = {
                            if (findNavController().currentDestination?.id == R.id.settingsFragment) {
                                findNavController().navigate(R.id.action_settingsFragment_to_editCategoriesFragment)
                            }
                        },
                        icon = BitmapPainter(endDrawable.toBitmap().asImageBitmap()),
                        text = context.resources.getString(R.string.edit_categories_title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        buttonColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp),
                        textColor = colorResource(R.color.textColor),
                        textSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start
                    )

                    VocableButtonWithEndImage(
                        onClick = {
                            if (findNavController().currentDestination?.id == R.id.settingsFragment) {
                                findNavController().navigate(R.id.action_settingsFragment_to_sensitivityFragment)
                            }
                        },
                        icon = BitmapPainter(endDrawable.toBitmap().asImageBitmap()),
                        text = context.resources.getString(R.string.timing_sensitivity_title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        buttonColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp),
                        textColor = colorResource(R.color.textColor),
                        textSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start
                    )

                    VocableButtonWithEndImage(
                        onClick = {
                            if (findNavController().currentDestination?.id == R.id.settingsFragment) {
                                findNavController().navigate(R.id.action_settingsFragment_to_selectionModeFragment)
                            }
                        },
                        icon = BitmapPainter(endDrawable.toBitmap().asImageBitmap()),
                        text = context.resources.getString(R.string.settings_selection_mode),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        buttonColor = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp),
                        textColor = colorResource(R.color.textColor),
                        textSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start
                    )

                }
            }
        }

        return binding.root
    }

    override fun getAllViews(): List<View> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.version.text = getString(R.string.version, BuildConfig.VERSION_NAME)

        binding.privacyPolicyButton.action = {
            showLeavingAppDialog {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY)))
            }
        }

        binding.contactDevsButton.action = {
            showLeavingAppDialog {
                val sendEmail = Intent(Intent.ACTION_SENDTO).apply {
                    data =
                        Uri.parse("$MAIL_TO${BuildConfig.VERSION_NAME}-${BuildConfig.VERSION_CODE}")
                }
                startActivity(sendEmail)
            }
        }

//        binding.settingsOptionsContainer.timingSensitivityButton.action = {
//
//        }

//        binding.settingsOptionsContainer.selectionModeButton.action = {
//            if (findNavController().currentDestination?.id == R.id.settingsFragment) {
//                findNavController().navigate(R.id.action_settingsFragment_to_selectionModeFragment)
//            }
//        }

//        binding.settingsOptionsContainer.editCategoriesButton.action = {
//
//        }
    }


    private fun showLeavingAppDialog(positiveAction: (() -> Unit)) {
        setSettingsButtonsEnabled(false)
        binding.settingsConfirmation.apply {
            dialogTitle.setText(R.string.settings_dialog_title)
            dialogMessage.setText(R.string.settings_dialog_message)
            dialogPositiveButton.apply {
                setText(R.string.settings_dialog_continue)
                action = {
                    positiveAction.invoke()
                    toggleDialogVisibility(false)

                    setSettingsButtonsEnabled(true)
                }
            }
            dialogNegativeButton.apply {
                setText(R.string.settings_dialog_cancel)
                action = {
                    toggleDialogVisibility(false)
                    setSettingsButtonsEnabled(true)
                }
            }
        }
        toggleDialogVisibility(true)
    }

    private fun setSettingsButtonsEnabled(enable: Boolean) {
        binding.apply {
            settingsCloseButton?.isEnabled = enable
            privacyPolicyButton.isEnabled = enable
            contactDevsButton.isEnabled = enable
//            settingsOptionsContainer.editCategoriesButton.isEnabled = enable
            //settingsOptionsContainer.resetAppButton.isEnabled = enable
            //settingsOptionsContainer.selectionModeButton.isEnabled = enable
            //settingsOptionsContainer.timingSensitivityButton.isEnabled = enable
        }
    }

    private fun toggleDialogVisibility(visible: Boolean) {
        binding.settingsConfirmation.root.isVisible = visible
    }
}

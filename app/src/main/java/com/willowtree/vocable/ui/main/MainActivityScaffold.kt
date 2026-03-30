package com.willowtree.vocable.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.willowtree.vocable.R
import com.willowtree.vocable.navigation.VocableNavHost

@Composable
fun MainActivityScaffold(
    pointerOffset: Offset,
    pointerVisible: Boolean,
    faceErrorVisible: Boolean,
    onNavControllerReady: (NavHostController) -> Unit,
    faceTrackContent: @Composable () -> Unit = {},
) {
    val navController = rememberNavController()
    DisposableEffect(navController) {
        onNavControllerReady(navController)
        onDispose { }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        faceTrackContent()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(R.color.colorPrimaryDark)),
        )

        VocableNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = dimensionResource(R.dimen.main_activity_top_bottom_margin)),
        )

        if (pointerVisible) {
            Image(
                painter = painterResource(R.drawable.pointer_background),
                contentDescription = null,
                modifier = Modifier
                    .offset {
                        IntOffset(
                            pointerOffset.x.toInt(),
                            pointerOffset.y.toInt(),
                        )
                    }
                    .size(dimensionResource(R.dimen.pointer_view_width_height)),
            )
        }

        if (faceErrorVisible) {
            val ctx = LocalContext.current
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = dimensionResource(R.dimen.settings_close_button_top_margin))
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(dimensionResource(R.dimen.highlighted_background_radius)))
                    .background(colorResource(R.color.errorColor))
                    .padding(
                        horizontal = dimensionResource(R.dimen.error_horizontal_padding),
                        vertical = dimensionResource(R.dimen.error_vertical_padding),
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_error),
                    contentDescription = null,
                    modifier = Modifier.padding(end = dimensionResource(R.dimen.error_drawable_padding)),
                )
                Text(
                    text = ctx.getString(R.string.error_move_closer),
                    color = colorResource(R.color.textColor),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                )
            }
        }
    }
}

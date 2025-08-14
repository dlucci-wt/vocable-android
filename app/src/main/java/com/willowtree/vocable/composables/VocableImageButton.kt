package com.willowtree.vocable.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.willowtree.vocable.R

@Composable
fun VocableImageButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    height : Dp = 72.dp,
    width : Dp = 72.dp,
    backgroundColor : Color = MaterialTheme.colorScheme.primary,
    contentDescription : String = "",
    painter : Painter = painterResource(R.drawable.ic_close),
    shape: Shape = RectangleShape,
    tint : Color = Color.White
) {

    val isPreview = LocalInspectionMode.current

    IconButton(
        onClick = onClick ,
        modifier = modifier
            .size(width = width, height = height)
            .background(color = backgroundColor, shape = shape)
    ) {
        val iconModifier = Modifier.fillMaxSize()
        if (!isPreview)
            Icon(
                painter = painter,
                contentDescription = contentDescription,
                modifier = iconModifier,
                tint = tint

            )
        else
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = contentDescription,
                modifier = iconModifier
            )
    }
}

@Preview(apiLevel = 35)
@Composable
fun PreviewVocableButton() {
    VocableImageButton(onClick = {})
}
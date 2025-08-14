package com.willowtree.vocable.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun VocableButtonWithEndImage(
    modifier: Modifier = Modifier,
    text: String = "",
    onClick: () -> Unit,
    icon: Painter,
    shape: Shape = RectangleShape,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.White,
    textSize: TextUnit = 16.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Center,
    buttonPadding : Dp = 8.dp,
    iconHeight : Dp = 32.dp,
    iconWidth : Dp = 32.dp
) {
    Button(
        onClick = onClick,
        modifier = modifier.background(shape = shape, color = buttonColor)
            .fillMaxWidth().padding(buttonPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = text,
                color = textColor,
                fontSize = textSize,
                fontWeight = fontWeight,
                textAlign = textAlign,
                modifier = Modifier.weight(1f))
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(height = iconHeight, width = iconWidth)
            )
        }
    }
}

@Preview(apiLevel = 34, showBackground = true)
@Composable
fun PreviewVocableButtonWithEndImage() {
    VocableButtonWithEndImage(
        onClick = {},
        text = "hello",
        icon = rememberVectorPainter(Icons.Filled.PlayArrow),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp)
    )
}
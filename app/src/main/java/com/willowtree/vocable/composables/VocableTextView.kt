package com.willowtree.vocable.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp


@Composable
fun VocableTextView(
    modifier: Modifier = Modifier,
    text: String = "",
    fontSize : TextUnit = 34.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    textColor : Color = MaterialTheme.colorScheme.primary,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = textColor,
        textAlign = textAlign,
        modifier = modifier
    )
}

@Preview(apiLevel = 34)
@Composable
fun PreviewVocableTextView() {
    VocableTextView(text = "Settings")
}
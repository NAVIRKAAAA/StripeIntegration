package com.app.stripeintegration.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PayItemComponent(
    value: PayItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val borderColor = if(selected) {
        Color.Red
    } else {
        Color.Black
    }

    Row(
        modifier = modifier.clickable(onClick = onClick)
    ) {

        Column(
            modifier = Modifier.border(BorderStroke(2.dp, borderColor)).padding(16.dp)
        ) {
            Text(
                text = value.name
            )

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "${(value.amount / 100)}$",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

}

@Preview
@Composable
fun PayItemComponentPreview() {

    PayItemComponent(
        value = PayItem.SMALL,
        selected = false,
        onClick = {}
    )

}
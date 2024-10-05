package com.app.stripeintegration.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PayContent(
    state: PayState,
    onPayItemClick: (PayItem) -> Unit,
    onPayClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onGooglePayClick: () -> Unit,
    onAddNewPaymentMethod: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Customer ID: ${state.customerId}",
            modifier = Modifier.padding(16.dp)
        )

        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {

            items(state.payItems) { item ->
                PayItemComponent(
                    value = item,
                    selected = state.selectedPayItem == item,
                    onClick = { onPayItemClick(item) }
                )
            }

        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        state.paymentResultMessage?.let {
            Text(text = it, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Button(
            onClick = onPayClick,
            enabled = state.paymentButtonsIsEnabled
        ) {
            Text("Pay by Payment form")
        }

        Button(
            onClick = onGooglePayClick,
            enabled = state.paymentButtonsIsEnabled
        ) {
            Text("Pay by Google Pay")
        }

        Button(
            onClick = onAddAddressClick,
            enabled = state.paymentButtonsIsEnabled
        ) {
            Text("Add Address")
        }

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Button(
            onClick = onAddNewPaymentMethod,
            enabled = state.customerId != null
        ) {
            Text("Add new Payment Method")
        }


        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
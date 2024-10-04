package com.app.stripeintegration.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.rememberPaymentSheet

@Composable
fun PayScreen(
    viewModel: PayViewModel,
    modifier: Modifier = Modifier
) {

    val state by viewModel.state.collectAsState()
    val paymentSheet = rememberPaymentSheet(viewModel::onPaymentSheetResult)

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                PayEvent.SetupPayItem -> {
                    val configuration = viewModel.getConfiguration()

                    val currentClientSecret = state.clientSecret

                    paymentSheet.presentWithPaymentIntent(
                        currentClientSecret,
                        PaymentSheet.Configuration(
                            merchantDisplayName = "My merchant name",
                            customer = configuration,
                            allowsDelayedPaymentMethods = true
                        )
                    )
                }
            }
        }
    }


    PayContent(
        modifier = modifier,
        state = state,
        onPayClick = viewModel::onPayClick,
        onPayItemClick = viewModel::onPayItemClick
    )

}
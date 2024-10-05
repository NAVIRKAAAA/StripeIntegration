package com.app.stripeintegration.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import com.stripe.android.googlepaylauncher.rememberGooglePayLauncher
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.rememberPaymentSheet

@Composable
fun PayScreen(
    viewModel: PayViewModel,
    modifier: Modifier = Modifier
) {

    val state by viewModel.state.collectAsState()
    val paymentSheet = rememberPaymentSheet(viewModel::onPaymentSheetResult)
    val googlePayLauncher = rememberGooglePayLauncher(
        config = GooglePayLauncher.Config(
            environment = GooglePayEnvironment.Test,
            merchantCountryCode = "US",
            merchantName = "Merchant Name"
        ),
        readyCallback = viewModel::onGooglePayReady,
        resultCallback = viewModel::onGooglePayResult
    )

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                PayEvent.SetupByPaymentForm -> {
                    val configuration = viewModel.getConfiguration()

                    val currentClientSecret = state.clientSecret

                    paymentSheet.presentWithPaymentIntent(
                        currentClientSecret,
                        PaymentSheet.Configuration(
                            merchantDisplayName = "Merchant Name",
                            customer = configuration,
                            allowsDelayedPaymentMethods = true
                        )
                    )
                }

                PayEvent.SetupByGooglePay -> {
                    val currentClientSecret = state.clientSecret

                    googlePayLauncher.presentForPaymentIntent(currentClientSecret)
                }
            }
        }
    }


    PayContent(
        modifier = modifier,
        state = state,
        onPayClick = viewModel::onPayClick,
        onPayItemClick = viewModel::onPayItemClick,
        onGooglePayClick = viewModel::onGooglePayClick
    )

}
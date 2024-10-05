package com.app.stripeintegration.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.app.stripeintegration.main.publishableKey
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import com.stripe.android.googlepaylauncher.rememberGooglePayLauncher
import com.stripe.android.paymentsheet.addresselement.rememberAddressLauncher
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
    val addressLauncher = rememberAddressLauncher(
        callback = viewModel::onPaymentAddressResult
    )

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                PayEvent.SetupByPaymentForm -> {
                    val configuration = viewModel.getCardPaymentConfiguration()
                    val currentClientSecret = state.clientSecret

                    paymentSheet.presentWithPaymentIntent(
                        currentClientSecret,
                        configuration
                    )
                }

                PayEvent.SetupByGooglePay -> {
                    val currentClientSecret = state.clientSecret

                    googlePayLauncher.presentForPaymentIntent(currentClientSecret)
                }

                PayEvent.SetupByAddress -> {
                    val configuration = viewModel.getAddressPaymentConfiguration()

                    addressLauncher.present(
                        publishableKey = publishableKey,
                        configuration = configuration
                    )
                }
            }
        }
    }


    PayContent(
        modifier = modifier,
        state = state,
        onPayClick = viewModel::onPayClick,
        onPayItemClick = viewModel::onPayItemClick,
        onGooglePayClick = viewModel::onGooglePayClick,
        onAddressPayClick = viewModel::onAddressPayClick
    )

}
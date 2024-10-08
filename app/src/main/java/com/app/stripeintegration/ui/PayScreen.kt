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
    val paymentSheet = rememberPaymentSheet(viewModel::onResult)
    val googlePayLauncher = rememberGooglePayLauncher(
        config = GooglePayLauncher.Config(
            environment = GooglePayEnvironment.Test,
            merchantCountryCode = "US",
            merchantName = "Merchant Name"
        ),
        readyCallback = viewModel::onGooglePayReady,
        resultCallback = viewModel::onResult
    )
    val addressLauncher = rememberAddressLauncher(
        callback = viewModel::onResult
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

                PayEvent.SetupAddAddress -> {
                    val configuration = viewModel.getAddAddressConfiguration()

                    addressLauncher.present(
                        publishableKey = publishableKey,
                        configuration = configuration
                    )
                }

                PayEvent.SetupAddNewPaymentMethod -> {
                    val currentClientSecret = state.clientSecret

                    paymentSheet.presentWithSetupIntent(
                        setupIntentClientSecret = currentClientSecret
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
        onAddAddressClick = viewModel::onAddAddressClick,
        onAddNewPaymentMethod = viewModel::onAddNewPaymentMethod
    )

}
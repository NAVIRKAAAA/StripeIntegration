package com.app.stripeintegration.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.app.stripeintegration.BuildConfig
import com.app.stripeintegration.ui.PayScreen
import com.app.stripeintegration.ui.PayViewModel
import com.app.stripeintegration.ui.theme.StripeIntegrationTheme
import com.app.stripeintegration.util.CustomerIdManager
import com.stripe.android.PaymentConfiguration

const val publishableKey = BuildConfig.publishableKey

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentConfiguration.init(this, publishableKey)

        setContent {
            StripeIntegrationTheme {

                val viewModel = PayViewModel(CustomerIdManager(this))
                PayScreen(viewModel = viewModel)

            }
        }
    }
}
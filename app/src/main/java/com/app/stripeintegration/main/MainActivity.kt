package com.app.stripeintegration.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.app.stripeintegration.ui.theme.StripeIntegrationTheme
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.app.stripeintegration.ui.PayScreen
import com.app.stripeintegration.ui.PayViewModel
import com.stripe.android.paymentsheet.PaymentSheet

class MainActivity : ComponentActivity() {
    private val publishableKey = "publishableKey"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentConfiguration.init(this, publishableKey)

        setContent {
            StripeIntegrationTheme {

                val viewModel by viewModels<PayViewModel>()
                PayScreen(viewModel = viewModel)

            }
        }
    }
}
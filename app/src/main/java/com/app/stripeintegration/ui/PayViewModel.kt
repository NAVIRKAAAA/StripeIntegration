package com.app.stripeintegration.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.stripeintegration.main.getApi
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.addresselement.AddressLauncher
import com.stripe.android.paymentsheet.addresselement.AddressLauncherResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class PayEvent {
    data object SetupByPaymentForm : PayEvent()
    data object SetupByGooglePay : PayEvent()
    data object SetupByAddress : PayEvent()
}

class PayViewModel : ViewModel() {

    private val api = getApi()

    private val _state = MutableStateFlow(PayState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<PayEvent>()

    fun getCardPaymentConfiguration(): PaymentSheet.Configuration {
        val customerConfiguration = PaymentSheet.CustomerConfiguration(
            id = state.value.customerId,
            ephemeralKeySecret = state.value.ephemeralKey
        )

        return PaymentSheet.Configuration(
            merchantDisplayName = "Merchant Name",
            customer = customerConfiguration,
            allowsDelayedPaymentMethods = true
        )
    }

    fun getAddressPaymentConfiguration(): AddressLauncher.Configuration {
        val configuration = AddressLauncher.Configuration(
            allowedCountries = setOf("US", "CA", "GB"),
            title = "Shipping Address",
            autocompleteCountries = setOf()
        )

        return configuration
    }

    fun onPayItemClick(payItem: PayItem) {
        viewModelScope.launch {
            _state.update { it.copy(selectedPayItem = payItem) }
        }
    }

    fun onPayClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            setCustomerId()
            event.emit(PayEvent.SetupByPaymentForm)
        }
    }

    fun onAddressPayClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
        }
        viewModelScope.launch(Dispatchers.IO) {
            setCustomerId()
            event.emit(PayEvent.SetupByAddress)
        }
    }

    fun onGooglePayClick() {
        viewModelScope.launch { _state.update { it.copy(isLoading = true) } }

        viewModelScope.launch(Dispatchers.IO) {
            setCustomerId()
            event.emit(PayEvent.SetupByGooglePay)
        }
    }

    private suspend fun setCustomerId() {
        val result = api.getCustomer()
        val resultBody = result.body()
        if (result.isSuccessful && resultBody != null) {
            _state.update { it.copy(customerId = resultBody.id) }
            setEphemeralKey()
        }
    }

    private suspend fun setEphemeralKey() {
        val result = api.getEphemeralKey(state.value.customerId)
        val resultBody = result.body()
        if (result.isSuccessful && resultBody != null) {
            _state.update { it.copy(ephemeralKey = resultBody.secret) }
            setPaymentIntent()
        }
    }

    private suspend fun setPaymentIntent() {
        val selectedPayItem = state.value.selectedPayItem ?: return

        val result =
            api.getPaymentIntent(state.value.customerId, selectedPayItem.amount, "usd", true)
        val resultBody = result.body()
        if (result.isSuccessful && resultBody != null) {
            _state.update { it.copy(clientSecret = resultBody.client_secret, isLoading = false) }
        }
    }

    fun onGooglePayReady(isReady: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(paymentResultMessage = "Google Pay is ready: $isReady") }
        }
    }

    fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        viewModelScope.launch {
            when (paymentSheetResult) {
                PaymentSheetResult.Canceled -> {
                    _state.update { it.copy(paymentResultMessage = "PaymentSheetResult.Canceled") }
                }

                PaymentSheetResult.Completed -> {
                    _state.update { it.copy(paymentResultMessage = "PaymentSheetResult.Completed") }
                }

                is PaymentSheetResult.Failed -> {
                    _state.update { it.copy(paymentResultMessage = "PaymentSheetResult.Failed: ${paymentSheetResult.error}") }
                }
            }
        }
    }

    fun onGooglePayResult(result: GooglePayLauncher.Result) {
        viewModelScope.launch {
            when (result) {
                GooglePayLauncher.Result.Completed -> {
                    _state.update { it.copy(paymentResultMessage = "GooglePayLauncher.Result.Completed") }
                }

                GooglePayLauncher.Result.Canceled -> {
                    _state.update { it.copy(paymentResultMessage = "GooglePayLauncher.Result.Canceled") }
                }

                is GooglePayLauncher.Result.Failed -> {
                    _state.update { it.copy(paymentResultMessage = "GooglePayLauncher.Result.Failed: ${result.error}") }
                }
            }
        }
    }

    fun onPaymentAddressResult(addressLauncherResult: AddressLauncherResult) {
        viewModelScope.launch {
            when (addressLauncherResult) {
                AddressLauncherResult.Canceled -> {
                    _state.update { it.copy(paymentResultMessage = "AddressLauncherResult.Canceled") }
                }

                is AddressLauncherResult.Succeeded -> {
                    _state.update { it.copy(paymentResultMessage = "AddressLauncherResult.Succeeded: ${addressLauncherResult.address}") }
                }
            }
        }
    }

}
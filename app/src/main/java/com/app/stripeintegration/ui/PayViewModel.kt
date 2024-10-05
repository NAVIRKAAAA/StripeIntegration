package com.app.stripeintegration.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.stripeintegration.main.getApi
import com.app.stripeintegration.util.MessageManager
import com.app.stripeintegration.util.apiHandler
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.addresselement.AddressLauncher
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
    private val messageManager = MessageManager()

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
            apiHandler { setCustomerId() }
            event.emit(PayEvent.SetupByPaymentForm)
        }
    }

    fun onAddressPayClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            apiHandler { setCustomerId() }
            event.emit(PayEvent.SetupByAddress)
        }
    }

    fun onGooglePayClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            apiHandler { setCustomerId() }
            event.emit(PayEvent.SetupByGooglePay)
        }
    }

    private suspend fun setCustomerId() {
        val result = api.getCustomer()

        _state.update { it.copy(customerId = result.id) }
        setEphemeralKey()
    }

    private suspend fun setEphemeralKey() {
        val result = api.getEphemeralKey(state.value.customerId)

        _state.update { it.copy(ephemeralKey = result.secret) }
        setPaymentIntent()
    }

    private suspend fun setPaymentIntent() {
        val selectedPayItem = state.value.selectedPayItem ?: return

        val result =
            api.getPaymentIntent(state.value.customerId, selectedPayItem.amount, "usd", true)

        _state.update { it.copy(clientSecret = result.client_secret, isLoading = false) }
    }

    fun onGooglePayReady(isReady: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(paymentResultMessage = "Google Pay is ready: $isReady") }
        }
    }

    fun <T : Any> onResult(result: T) {
        viewModelScope.launch {
            val message = messageManager.getByResult(result)
            _state.update { it.copy(paymentResultMessage = message) }
        }
    }

}
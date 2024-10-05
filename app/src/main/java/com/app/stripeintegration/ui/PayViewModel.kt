package com.app.stripeintegration.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.stripeintegration.main.getApi
import com.app.stripeintegration.util.CustomerIdManager
import com.app.stripeintegration.util.apiHandler
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.addresselement.AddressLauncher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class PayEvent {
    data object SetupByPaymentForm : PayEvent()
    data object SetupByGooglePay : PayEvent()
    data object SetupAddAddress : PayEvent()
    data object SetupAddNewPaymentMethod : PayEvent()
}

class PayViewModel(
    private val customerIdManager: CustomerIdManager
) : ViewModel() {

    private val api = getApi()

    private val _state = MutableStateFlow(PayState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<PayEvent>()

    init {
        viewModelScope.launch {
            apiHandler { setCustomerId() }
        }
    }


    fun onAddNewPaymentMethod() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            apiHandler {
                setEphemeralKey()
                setSetupIntent()
            }
            event.emit(PayEvent.SetupAddNewPaymentMethod)
        }
    }


    fun getCardPaymentConfiguration(): PaymentSheet.Configuration? {
        val customerId = state.value.customerId ?: return null
        val customerConfiguration = PaymentSheet.CustomerConfiguration(
            id = customerId,
            ephemeralKeySecret = state.value.ephemeralKey
        )

        return PaymentSheet.Configuration(
            merchantDisplayName = "Merchant Name",
            customer = customerConfiguration,
            allowsDelayedPaymentMethods = true
        )
    }

    fun getAddAddressConfiguration(): AddressLauncher.Configuration {
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
            apiHandler {
                setEphemeralKey()
                setPaymentIntent()
            }
            event.emit(PayEvent.SetupByPaymentForm)
        }
    }

    fun onAddAddressClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            apiHandler {
                setEphemeralKey()
                setPaymentIntent()
            }
            event.emit(PayEvent.SetupAddAddress)
        }
    }

    fun onGooglePayClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            apiHandler {
                setEphemeralKey()
                setPaymentIntent()
            }
            event.emit(PayEvent.SetupByGooglePay)
        }
    }

    private suspend fun setCustomerId() {
        val savedCustomerId = customerIdManager.get().first()
        val customerId = if (savedCustomerId == null) {
            val id = api.getCustomer().id
            customerIdManager.set(id)
            id
        } else {
            savedCustomerId
        }

        _state.update { it.copy(customerId = customerId) }
    }

    private suspend fun setEphemeralKey() {
        val customerId = state.value.customerId ?: return
        val result = api.getEphemeralKey(customerId)

        _state.update { it.copy(ephemeralKey = result.secret) }
    }

    private suspend fun setPaymentIntent() {
        val customerId = state.value.customerId ?: return
        val selectedPayItem = state.value.selectedPayItem ?: return

        val result =
            api.getPaymentIntent(customerId, selectedPayItem.amount, "usd", true)

        _state.update { it.copy(clientSecret = result.client_secret, isLoading = false) }
    }

    private suspend fun setSetupIntent() {
        val customerId = state.value.customerId ?: return

        val result = api.getSetupIntent(customerId, true)

        _state.update { it.copy(clientSecret = result.client_secret, isLoading = false) }
    }

    fun onGooglePayReady(isReady: Boolean) {
        viewModelScope.launch {
            _state.update { it.copy(paymentResultMessage = "Google Pay is ready: $isReady") }
        }
    }

    fun <T : Any> onResult(result: T) {
        viewModelScope.launch {
            _state.update { it.copy(paymentResultMessage = "Result: $result") }
        }
    }
}
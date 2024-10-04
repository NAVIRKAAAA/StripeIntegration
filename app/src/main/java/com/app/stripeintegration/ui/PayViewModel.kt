package com.app.stripeintegration.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.stripeintegration.main.getApi
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class PayEvent {
    data object SetupPayItem : PayEvent()
}

class PayViewModel : ViewModel() {

    private val api = getApi()

    private val _state = MutableStateFlow(PayState())
    val state = _state.asStateFlow()

    val event = MutableSharedFlow<PayEvent>()

    fun getConfiguration(): PaymentSheet.CustomerConfiguration {
        val configuration = PaymentSheet.CustomerConfiguration(
            id = state.value.customerId,
            ephemeralKeySecret = state.value.ephemeralKey
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
            setCustomerId()
        }
    }

    private fun setCustomerId() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = api.getCustomer()
            val resultBody = result.body()
            if (result.isSuccessful && resultBody != null) {
                _state.update { it.copy(customerId = resultBody.id) }
                setEphemeralKey()
            }
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
            event.emit(PayEvent.SetupPayItem)
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

}
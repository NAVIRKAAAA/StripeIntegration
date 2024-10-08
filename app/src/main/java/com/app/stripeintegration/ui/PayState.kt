package com.app.stripeintegration.ui

data class PayState(
    val payItems: List<PayItem> = PayItem.entries,
    val selectedPayItem: PayItem? = null,
    val isLoading: Boolean = false,

    val customerId: String? = null,
    val ephemeralKey: String = "",
    val clientSecret: String = "",
    val paymentResultMessage: String? = null
) {
    val paymentButtonsIsEnabled: Boolean
        get() = selectedPayItem != null && customerId != null
}

enum class PayItem(val amount: Int) {
    SMALL(100),
    MEDIUM(200),
    LARGE(100 * 100)
}

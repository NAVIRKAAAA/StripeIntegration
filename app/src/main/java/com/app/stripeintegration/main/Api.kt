package com.app.stripeintegration.main

import com.app.stripeintegration.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

private const val AUTH_TOKEN = BuildConfig.authToken

data class Customer(
    val id: String
)

data class EphemeralKey(
    val id: String,
    val secret: String
)

data class PaymentIntent(
    val id: String,
    val client_secret: String
)

interface Api {

    @Headers("Authorization: Bearer $AUTH_TOKEN")
    @POST("v1/customers")
    suspend fun getCustomer(): Customer

    @Headers(
        "Authorization: Bearer $AUTH_TOKEN",
        "Stripe-Version: 2024-09-30.acacia"
    )
    @POST("v1/ephemeral_keys")
    suspend fun getEphemeralKey(
        @Query("customer") customer: String
    ): EphemeralKey

    @Headers("Authorization: Bearer $AUTH_TOKEN")
    @POST("v1/payment_intents")
    suspend fun getPaymentIntent(
        @Query("customer") customer: String,
        @Query("amount") amount: Int,
        @Query("currency") currency: String,
        @Query("automatic_payment_methods[enabled]") automatePay: Boolean
    ): PaymentIntent
}

fun getApi(): Api {
    return Retrofit.Builder()
        .baseUrl("https://api.stripe.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Api::class.java)
}
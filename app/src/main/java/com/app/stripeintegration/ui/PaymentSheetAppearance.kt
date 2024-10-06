package com.app.stripeintegration.ui

import androidx.compose.ui.graphics.Color
import com.stripe.android.paymentsheet.PaymentSheet

val paymentSheetAppearance = PaymentSheet.Appearance(
    colorsLight = PaymentSheet.Colors(
        primary = Color(red = 36, green = 36, blue = 47),
        surface = Color.White,
        component = Color(red = 243, green = 248, blue = 245),
        componentBorder = Color.Transparent,
        componentDivider = Color.Black,
        onComponent = Color.Black,
        subtitle = Color.Black,
        placeholderText = Color(red = 115, green = 117, blue = 123),
        onSurface = Color.Black,
        appBarIcon = Color.Black,
        error = Color.Red,
    ),
    shapes = PaymentSheet.Shapes(
        cornerRadiusDp = 12.0f,
        borderStrokeWidthDp = 0.5f
    ),
    primaryButton = PaymentSheet.PrimaryButton(
        shape = PaymentSheet.PrimaryButtonShape(
            cornerRadiusDp = 20f
        ),
    )
)
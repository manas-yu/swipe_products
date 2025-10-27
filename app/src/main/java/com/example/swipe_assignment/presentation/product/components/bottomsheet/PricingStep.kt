package com.example.swipe_assignment.presentation.product.components.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PricingStep(
    price: String,
    onPriceChange: (String) -> Unit,
    tax: String,
    onTaxChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = price,
            onValueChange = { v ->
                if (v.isEmpty() || v.matches(Regex("""\d{0,9}(\.\d{0,2})?"""))) onPriceChange(v)
            },
            label = { Text("Price (₹)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = tax,
            onValueChange = { v ->
                if (v.isEmpty() || v.matches(Regex("""\d{0,3}(\.\d{0,2})?"""))) onTaxChange(v)
            },
            label = { Text("Tax (%)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onNext() }),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
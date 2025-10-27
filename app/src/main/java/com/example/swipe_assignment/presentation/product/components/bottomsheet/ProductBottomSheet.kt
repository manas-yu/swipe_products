package com.example.swipe_assignment.presentation.product.components.bottomsheet

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.swipe_assignment.presentation.product.ProductViewModel

private enum class ProductStep { Details, Pricing, Image, Review }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductBottomSheet(
    viewModel: ProductViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val uniqueProductTypes by remember(uiState.products) {
        derivedStateOf { uiState.products.map { it.productType }.distinct().sorted() }
    }

    var step by rememberSaveable { mutableIntStateOf(ProductStep.Details.ordinal) }
    var productName by rememberSaveable { mutableStateOf("") }
    var productType by rememberSaveable { mutableStateOf("") }
    var isTypeExpanded by remember { mutableStateOf(false) }
    var price by rememberSaveable { mutableStateOf("") }
    var tax by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val totalSteps = ProductStep.entries.size
    val currentStep = ProductStep.entries[step]
    val progressTarget = (step.toFloat() / (totalSteps - 1).coerceAtLeast(1))
    val progress by animateFloatAsState(progressTarget, label = "progress")

    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
        }
    LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(productName, productType, price, tax) { error = null }

    fun validateAndNext() {
        when (ProductStep.entries[step]) {
            ProductStep.Details -> {
                when {
                    productName.isBlank() -> error = "Product Name is required"
                    productType.isBlank() -> error = "Product Type is required"
                    else -> {
                        step++; error = null
                    }
                }
            }

            ProductStep.Pricing -> {
                val priceOk = price.matches(Regex("""\d{1,9}(\.\d{1,2})?"""))
                val taxOk = tax.matches(Regex("""\d{1,3}(\.\d{1,2})?""")) &&
                        (tax.toFloatOrNull() ?: 101f) in 0f..100f
                when {
                    price.isBlank() -> error = "Price is required"
                    !priceOk -> error = "Enter a valid price (max 2 decimals)"
                    tax.isBlank() -> error = "Tax Rate is required"
                    !taxOk -> error = "Tax must be between 0 and 100"
                    else -> {
                        step++; error = null
                    }
                }
            }

            ProductStep.Image -> {
                step++; error = null
            }

            ProductStep.Review -> {
                val p = price.toDoubleOrNull()
                val t = tax.toDoubleOrNull()
                if (p == null || t == null) {
                    error = "Invalid number format"
                    return
                }
                isUploading = true
                try {
                    viewModel.addProduct(
                        productName.trim(),
                        productType.trim(),
                        p,
                        t,
                        imageUri
                    )
                    onDismiss()
                } catch (_: Exception) {
                    onDismiss()
                } finally {
                    isUploading = false
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 6.dp,
        dragHandle = { Box(Modifier.padding(top = 12.dp)) },
        contentWindowInsets = { WindowInsets(0) }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .padding(horizontal = 20.dp)
        ) {
            StepHeader(step, totalSteps, progress)

            Spacer(Modifier.height(12.dp))

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "stepTransition"
            ) { current ->
                when (current) {
                    ProductStep.Details -> DetailsStep(
                        productName,
                        { productName = it },
                        productType,
                        { productType = it },
                        isTypeExpanded,
                        { isTypeExpanded = it },
                        uniqueProductTypes,
                        { validateAndNext() }
                    )

                    ProductStep.Pricing -> PricingStep(
                        price,
                        { price = it },
                        tax,
                        { tax = it },
                        { validateAndNext() })

                    ProductStep.Image -> ImageStep(
                        imageUri,
                        { imagePicker.launch("image/*") },
                        { imageUri = null })

                    ProductStep.Review -> ReviewStep(productName, productType, price, tax, imageUri)
                }
            }

            Spacer(Modifier.height(12.dp))

            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (step > 0) {
                    Button(
                        onClick = { step-- },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors()
                    ) { Text("Back") }
                }
                Button(
                    onClick = { validateAndNext() },
                    enabled = !isUploading,
                    modifier = Modifier.weight(1f)
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            when (currentStep) {
                                ProductStep.Image -> if (imageUri == null) "Skip" else "Next"
                                ProductStep.Review -> "Add Product"
                                else -> "Next"
                            }
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}




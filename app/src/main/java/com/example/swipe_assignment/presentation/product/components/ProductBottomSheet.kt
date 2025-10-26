package com.example.swipe_assignment.presentation.product.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.swipe_assignment.R
import com.example.swipe_assignment.presentation.product.ProductViewModel
import kotlin.math.roundToInt

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

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(productName, productType, price, tax) { error = null }

    fun validateAndNext() {
        when (ProductStep.entries[step]) {
            ProductStep.Details -> {
                when {
                    productName.isBlank() -> error = "Product Name is required"
                    productType.isBlank() -> error = "Product Type is required"
                    else -> { step++; error = null }
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
                    else -> { step++; error = null }
                }
            }
            ProductStep.Image -> { step++; error = null }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentWindowInsets = { WindowInsets(0) }
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f) // force bottom-half height
            ) {
                Column(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(bottom = 12.dp)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProductStep.entries.forEachIndexed { index, _ ->
                                val active = index <= step
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (active) MaterialTheme.colorScheme.primary
                                            else ProgressIndicatorDefaults.linearTrackColor
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${index + 1}",
                                        color = if (active) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .zIndex(-1f),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = ProgressIndicatorDefaults.linearTrackColor,
                            progress = { progress }
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (currentStep) {
                            ProductStep.Details -> {
                                OutlinedTextField(
                                    value = productName,
                                    onValueChange = { productName = it },
                                    label = { Text("Product Name") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next,
                                        capitalization = KeyboardCapitalization.Words
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                ExposedDropdownMenuBox(
                                    expanded = isTypeExpanded,
                                    onExpandedChange = { isTypeExpanded = !isTypeExpanded },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = productType,
                                        onValueChange = {
                                            productType = it
                                            isTypeExpanded = true
                                        },
                                        label = { Text("Product Type") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = isTypeExpanded
                                            )
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                        keyboardActions = KeyboardActions(onDone = { validateAndNext() })
                                    )
                                    DropdownMenu(
                                        expanded = isTypeExpanded,
                                        onDismissRequest = { isTypeExpanded = false }
                                    ) {
                                        if (productType.isNotBlank() && !uniqueProductTypes.contains(productType)) {
                                            DropdownMenuItem(
                                                text = { Text("Use \"$productType\"") },
                                                onClick = {
                                                    productType = productType.trim()
                                                    isTypeExpanded = false
                                                },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                            )
                                        }
                                        uniqueProductTypes.forEach { type ->
                                            DropdownMenuItem(
                                                text = { Text(type) },
                                                onClick = {
                                                    productType = type
                                                    isTypeExpanded = false
                                                },
                                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                            )
                                        }
                                    }
                                }
                            }
                            ProductStep.Pricing -> {
                                OutlinedTextField(
                                    value = price,
                                    onValueChange = { v ->
                                        if (v.isEmpty() || v.matches(Regex("""\d{0,9}(\.\d{0,2})?""")))
                                            price = v
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
                                        val ok = v.isEmpty() || v.matches(Regex("""\d{0,3}(\.\d{0,2})?"""))
                                        if (ok) tax = v
                                    },
                                    label = { Text("Tax (%)") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { validateAndNext() }),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                            ProductStep.Image -> {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 220.dp)
                                            .clickable { imagePicker.launch("image/*") },
                                        shape = RoundedCornerShape(16.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                                        )
                                    ) {
                                        if (imageUri == null) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(R.drawable.image_plus),
                                                    contentDescription = null
                                                )
                                                Spacer(Modifier.height(8.dp))
                                                Text(
                                                    "Upload Image (optional)",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        } else {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(200.dp)
                                            ) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(imageUri)
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .matchParentSize()
                                                        .clip(RoundedCornerShape(16.dp)),
                                                    placeholder = painterResource(R.drawable.ic_launcher_foreground)
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .padding(8.dp)
                                                        .size(32.dp)
                                                        .align(Alignment.TopEnd)
                                                        .clip(CircleShape)
                                                        .background(
                                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                                        )
                                                        .clickable { imageUri = null },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Default.Close, contentDescription = null)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            ProductStep.Review -> {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column {
                                        if (imageUri != null) {
                                            AsyncImage(
                                                model = imageUri,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .aspectRatio(1.8f)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .padding(8.dp),
                                                placeholder = painterResource(R.drawable.ic_launcher_foreground)
                                            )
                                        }
                                        Column(Modifier.padding(12.dp)) {
                                            Text(
                                                productName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text("₹$price", style = MaterialTheme.typography.bodyMedium)
                                            val taxShow = tax.toFloatOrNull()?.roundToInt()?.toString() ?: tax
                                            Text(
                                                "Tax: $taxShow%",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = productType,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier
                                                .align(Alignment.End)
                                                .background(
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    shape = RoundedCornerShape(topStart = 8.dp)
                                                )
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        )

                                    }
                                }
                            }
                        }
                    }

                    if (error != null) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.size(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                                    text = when (currentStep) {
                                        ProductStep.Image -> if (imageUri == null) "Skip" else "Next"
                                        ProductStep.Review -> "Add Product"
                                        else -> "Next"
                                    }
                                )
                                Spacer(Modifier.size(6.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

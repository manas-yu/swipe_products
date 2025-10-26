package com.example.swipe_assignment.presentation.product.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.swipe_assignment.R
import com.example.swipe_assignment.data.local.entity.ProductEntity
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProductItem(
    product: ProductEntity,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val rupee = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 2
            currency = java.util.Currency.getInstance("INR")
        }
    }

    ElevatedCard(
        onClick = {
            if (!product.image.isNullOrBlank()) showDialog = true
            onClick?.invoke()
        },
        modifier = modifier.semantics {
            contentDescription = "Product card ${product.productName}"
        },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
    ) {
        Box(Modifier.fillMaxWidth()) {
            Column {
                ProductImage(
                    imageUrlOrEmpty = product.image.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(196.dp)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .semantics { contentDescription = "Product image preview" },
                )

                Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = product.productName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    rupee.format(product.price),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = AssistChipDefaults.assistChipBorder(enabled = false),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        Spacer(Modifier.width(10.dp))
                        AssistChip(
                            onClick = {},
                            label = { Text("Tax: ${product.tax}%") },
                            shape = RoundedCornerShape(10.dp),
                            border = AssistChipDefaults.assistChipBorder(enabled = false),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            if (product.productType.isNotBlank()) {
                TypeBadge(
                    text = product.productType,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                )
            }
        }
    }

    if (showDialog) {
        ImagePreviewDialog(
            imageUrl = product.image.orEmpty(),
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun ProductImage(
    imageUrlOrEmpty: String,
    modifier: Modifier = Modifier
) {
    val hasUrl = imageUrlOrEmpty.isNotBlank()
    val ctx = LocalContext.current

    SubcomposeAsyncImage(
        model = if (hasUrl) {
            ImageRequest.Builder(ctx).data(imageUrlOrEmpty).crossfade(true).build()
        } else null,
        contentDescription = "Product image",
        modifier = modifier.fillMaxWidth(),
        contentScale = if (hasUrl) ContentScale.Crop else ContentScale.Inside,
        loading = {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(strokeWidth = 2.dp)
            }
        },
        error = {
            Image(
                painter = painterResource(R.drawable.no_image),
                contentDescription = "No image available",
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Inside
            )
        },
        success = { SubcomposeAsyncImageContent() }
    )
}

@Composable
private fun TypeBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 0.dp
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}


@Composable
private fun ImagePreviewDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 360.dp)
                .background(Color.Black.copy(alpha = 0.96f), RoundedCornerShape(16.dp))
                .padding(10.dp)
                .semantics { contentDescription = "Zoomable image dialog" }
        ) {
            ZoomableImage(
                imageUrl = imageUrl,
                modifier = Modifier.fillMaxWidth(),
                enableRotation = false
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun ZoomableImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    minScale: Float = 1f,
    maxScale: Float = 4f,
    enableRotation: Boolean = false
) {
    val ctx = LocalContext.current
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val scale = remember { Animatable(1f) }
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var rotation by remember { mutableFloatStateOf(0f) }

    val scope = rememberCoroutineScope()

    val transformState = rememberTransformableState { zoomChange, panChange, rotationChange ->
        val newScale = (scale.value * zoomChange).coerceIn(minScale, maxScale)

        scope.launch {
            scale.snapTo(newScale)
            val clamped = clampOffset(offset.value + panChange, newScale, containerSize)
            offset.snapTo(clamped)
        }

        if (enableRotation) {
            rotation += rotationChange
        }
    }

    val onDoubleTap: () -> Unit = {
        scope.launch {
            if (scale.value < 1.5f) {
                scale.animateTo(2f, tween(220))
            } else {
                scale.animateTo(1f, tween(200))
                offset.animateTo(Offset.Zero, tween(200))
                rotation = 0f
            }
            val clamped = clampOffset(offset.value, scale.value, containerSize)
            if (clamped != offset.value) offset.animateTo(clamped, tween(160))
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { containerSize = it }
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { onDoubleTap() })
            }
            .graphicsLayer {
                // Apply current transform
                scaleX = scale.value
                scaleY = scale.value
                rotationZ = if (enableRotation) rotation else 0f
                translationX = offset.value.x
                translationY = offset.value.y
                clip = true
            }
            .transformable(transformState)
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(ctx).data(imageUrl).crossfade(true).build(),
            contentDescription = "Zoomable image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            loading = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 2.dp, color = Color.White)
                }
            },
            error = {
                Image(
                    painter = painterResource(R.drawable.no_image),
                    contentDescription = "No image available",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Inside
                )
            },
            success = { SubcomposeAsyncImageContent() }
        )

        val hintAlpha by animateFloatAsState(
            targetValue = if (scale.value == 1f) 1f else 0f,
            animationSpec = tween(300), label = "hintAlpha"
        )
        if (hintAlpha > 0f) {
            Text(
                "Double-tap to zoom",
                color = Color.White.copy(alpha = 0.6f * hintAlpha),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            )
        }
    }

    LaunchedEffect(containerSize) {
        val clamped = clampOffset(offset.value, scale.value, containerSize)
        if (clamped != offset.value) offset.snapTo(clamped)
    }

}

private fun clampOffset(current: Offset, scale: Float, container: IntSize): Offset {
    if (container.width == 0 || container.height == 0) return Offset.Zero
    val w = container.width.toFloat()
    val h = container.height.toFloat()

    if (scale <= 1f) return Offset.Zero

    val maxX = (w * (scale - 1f)) / 2f
    val maxY = (h * (scale - 1f)) / 2f

    val clampedX = current.x.coerceIn(-maxX, maxX)
    val clampedY = current.y.coerceIn(-maxY, maxY)
    return Offset(clampedX, clampedY)
}

package com.example.swipe_assignment.util


import androidx.compose.ui.graphics.Color

object Constants {
    const val BASE_URL = "https://app.getswipe.in/api/public/"
    const val PENDING_UPLOADS_TABLE = "pending_uploads"
    const val PRODUCTS_TABLE = "products"
}

enum class Progress(val color: Color) {
    Pending(Color.Yellow),
    Uploaded(Color.Green),
    Failed(Color.Red)
}
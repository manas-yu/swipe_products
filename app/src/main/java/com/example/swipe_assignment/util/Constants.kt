package com.example.swipe_assignment.util


import androidx.compose.ui.graphics.Color

object Constants {
  const val BASE_URL = "https://app.getswipe.in/api/public/"
}

enum class Progress(val color: Color) {
  Pending(Color.Yellow),
  Uploaded(Color.Green),
  Failed(Color.Red)
}
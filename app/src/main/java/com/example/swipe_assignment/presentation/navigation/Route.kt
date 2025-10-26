package com.example.swipe_assignment.presentation.navigation

import androidx.navigation.NamedNavArgument

sealed class Route(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object ProductScreen : Route(route = "ProductScreen")
    data object NotificationScreen : Route(route = "NotificationScreen")
}
package com.example.swipe_assignment.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.swipe_assignment.presentation.notification.NotificationScreen
import com.example.swipe_assignment.presentation.product.ProductListScreen

@Composable
fun Navigation(navController: NavHostController = rememberNavController()) {


    NavHost(
        navController = navController,
        startDestination = Route.ProductScreen.route
    ) {
        composable(route = Route.ProductScreen.route) {
            ProductListScreen {
                navController.navigate(Route.NotificationScreen.route)
            }
        }
        composable(route = Route.NotificationScreen.route) {
            NotificationScreen {
                navController.popBackStack()
            }
        }
    }
}
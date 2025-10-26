package com.example.swipe_assignment.presentation.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.swipe_assignment.R
import com.example.swipe_assignment.presentation.notification.components.NotificationItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- Accompanist SwipeRefresh setup ---
    var refreshing by remember { mutableStateOf(false) }
    val swipeState = rememberSwipeRefreshState(isRefreshing = refreshing)

    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(1_000) // Small UX delay to show refresh
            viewModel.fetchAll()
            refreshing = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications") },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                navigationIcon = {
                    IconButton(onClick = navigateToHome) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = swipeState,
            onRefresh = { refreshing = true },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.notificationList.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                    ) {
                        itemsIndexed(uiState.notificationList) { index, item ->
                            NotificationItem(item = item)
                            if (index != uiState.notificationList.lastIndex) {
                                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }

                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.no_item),
                            contentDescription = "No data"
                        )
                    }
                }
            }
        }
    }
}

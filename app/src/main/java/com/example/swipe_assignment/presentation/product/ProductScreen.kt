package com.example.swipe_assignment.presentation.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.swipe_assignment.R
import com.example.swipe_assignment.presentation.product.components.ProductBottomSheet
import com.example.swipe_assignment.presentation.product.components.ProductItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel = hiltViewModel(),
    onNavigateToNotification: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // --- Accompanist SwipeRefresh ---
    var refreshing by remember { mutableStateOf(false) }
    val swipeState = rememberSwipeRefreshState(isRefreshing = refreshing)
    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(1_000)           // optional UX pause
            viewModel.loadProducts()
            refreshing = false
        }
    }

    var isSearchBarVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isBottomSheetVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            if (error.isNotBlank()) {
                snackbarHostState.showSnackbar(message = error, duration = SnackbarDuration.Short)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            if (!isSearchBarVisible) {
                TopAppBar(
                    title = {
                        Image(
                            painter = painterResource(
                                id = if (isSystemInDarkTheme()) R.drawable.logo_night else R.drawable.logo
                            ),
                            contentDescription = "logo",
                            modifier = Modifier
                                .size(80.dp)
                                .scale(1.5f)
                                .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        )
                    },
                    scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                    actions = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable { onNavigateToNotification() }
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (uiState.unViewedCount > 0) {
                                            Badge(
                                                containerColor = Color.Red,
                                                contentColor = Color.White
                                            ) {
                                                Text("${uiState.unViewedCount}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Notification"
                                    )
                                }
                            }
                            IconButton(onClick = { isSearchBarVisible = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    }
                )
            } else {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { q ->
                        searchQuery = q
                        viewModel.updateSearchQuery(q)
                    },
                    onSearch = { viewModel.updateSearchQuery(searchQuery) },
                    placeholder = { Text("Search") },
                    active = isSearchBarVisible,
                    onActiveChange = { active ->
                        if (!active) {
                            isSearchBarVisible = false
                            searchQuery = ""
                            viewModel.updateSearchQuery("")
                        }
                    },
                    leadingIcon = {
                        IconButton(onClick = {
                            isSearchBarVisible = false
                            searchQuery = ""
                            viewModel.updateSearchQuery("")
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                ) {
                    Column(modifier = Modifier.fillMaxHeight()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.searchList.size) { i ->
                                val product = uiState.searchList[i]
                                ProductItem(product)
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isBottomSheetVisible = true },
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                uiState.products.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.no_item),
                            contentDescription = "No items",
                            colorFilter = ColorFilter.tint(Color.Gray)
                        )

                    }
                }

                else -> {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        columns = GridCells.Adaptive(180.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.products.size) { i ->
                            val product = uiState.products[i]
                            ProductItem(product = product)
                        }
                    }
                }
            }
        }

        if (isBottomSheetVisible) {
            ProductBottomSheet(
                viewModel = viewModel,
                onDismiss = { isBottomSheetVisible = false }
            )
        }
    }
}

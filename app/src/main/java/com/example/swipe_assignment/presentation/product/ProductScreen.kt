package com.example.swipe_assignment.presentation.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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

    var refreshing by remember { mutableStateOf(false) }
    val swipeState = rememberSwipeRefreshState(isRefreshing = refreshing)
    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(600)
            viewModel.loadProducts()
            refreshing = false
        }
    }

    var isSearchBarVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

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

    LaunchedEffect(isSearchBarVisible) {
        if (isSearchBarVisible) {
            delay(50)
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(searchQuery, isSearchBarVisible) {
        if (isSearchBarVisible) {
            delay(250)
            viewModel.updateSearchQuery(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            if (!isSearchBarVisible) {
                TopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.logo_night else R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier
                                .size(84.dp)
                                .scale(1.0f)
                                .padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
                        )
                    },
                    actions = {
                        IconButton(onClick = { isSearchBarVisible = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onNavigateToNotification) {
                            BadgedBox(badge = {
                                if (uiState.unViewedCount > 0) {
                                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                                        Text("${uiState.unViewedCount}")
                                    }
                                }
                            }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications"
                                )
                            }
                        }
                    },
                    scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
                )
            } else {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        keyboard?.hide()
                        viewModel.updateSearchQuery(searchQuery)
                    },
                    active = isSearchBarVisible,
                    onActiveChange = { active ->
                        if (!active) {
                            isSearchBarVisible = false
                            searchQuery = ""
                            viewModel.updateSearchQuery("")
                            keyboard?.hide()
                        }
                    },
                    placeholder = { Text("Search products") },
                    leadingIcon = {
                        IconButton(onClick = {
                            isSearchBarVisible = false
                            searchQuery = ""
                            viewModel.updateSearchQuery("")
                            keyboard?.hide()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.updateSearchQuery("")
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
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
                                ProductItem(uiState.searchList[i])
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
            ) { Icon(Icons.Default.Add, contentDescription = null) }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                isSearchBarVisible && searchQuery.isNotBlank() -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.searchList.size) { i ->
                            ProductItem(uiState.searchList[i])
                        }
                    }
                }

                uiState.products.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.no_item),
                            contentDescription = null,
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
                            ProductItem(product = uiState.products[i])
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

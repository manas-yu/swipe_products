package com.example.swipe_assignment.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipe_assignment.domain.model.ErrorModel
import com.example.swipe_assignment.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        fetchAll()
        viewModelScope.launch{ repository.markAsViewed() }
    }

    fun fetchAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getAll().collect { result ->
                val products = result.data ?: emptyList()
                _uiState.update { state ->
                    when (result) {
                        is ErrorModel.Success -> {
                            state.copy(
                                notificationList = products,
                                isLoading = false,
                                error = null
                            )
                        }

                        is ErrorModel.Error -> state.copy(
                            isLoading = false,
                            error = result.message
                        )

                        is ErrorModel.Loading -> state.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }
}
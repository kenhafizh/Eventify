package com.example.technical_test_indomarco.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.technical_test_indomarco.domain.model.Event
import com.example.technical_test_indomarco.domain.usecase.SaveEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SaveEventState {
    object Initial    : SaveEventState()
    object Loading : SaveEventState()
    object Success : SaveEventState()
    data class Error(val message: String) : SaveEventState()
}

class AddEventViewModel(
    private val saveEventUseCase: SaveEventUseCase
) : ViewModel() {

    private val _saveState = MutableStateFlow<SaveEventState>(SaveEventState.Initial)
    val saveState: StateFlow<SaveEventState> = _saveState.asStateFlow()

    fun publishEvent(
        eventName: String,
        bannerPath: String,
        startDate: String,
        startTime: String,
        endDate: String,
        endTime: String,
        location: String,
        organizer: String,
        description: String
    ) {
        viewModelScope.launch {
            _saveState.value = SaveEventState.Loading
            try {
                saveEventUseCase(
                    Event(
                        eventName   = eventName,
                        bannerPath  = bannerPath,
                        startDate   = startDate,
                        startTime   = startTime,
                        endDate     = endDate,
                        endTime     = endTime,
                        location    = location,
                        organizer   = organizer,
                        description = description
                    )
                )
                _saveState.value = SaveEventState.Success
            } catch (e: Exception) {
                _saveState.value = SaveEventState.Error("Failed to save event: ${e.message}")
            }
        }
    }


    class Factory(private val saveEventUseCase: SaveEventUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddEventViewModel(saveEventUseCase) as T
        }
    }
}

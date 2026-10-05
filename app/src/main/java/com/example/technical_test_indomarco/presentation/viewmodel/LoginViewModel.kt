package com.example.technical_test_indomarco.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.technical_test_indomarco.domain.model.LoginResult
import com.example.technical_test_indomarco.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            _loginState.value = when (val result = loginUseCase(username, password)) {
                is LoginResult.Success -> LoginState.Success(result.message)
                is LoginResult.Error   -> LoginState.Error(result.message)
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    class Factory(private val useCase: LoginUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(useCase) as T
        }
    }
}

sealed class LoginState {
    object Idle    : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val message: String) : LoginState()
}
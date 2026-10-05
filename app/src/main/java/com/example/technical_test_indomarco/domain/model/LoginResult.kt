package com.example.technical_test_indomarco.domain.model

sealed class LoginResult {
    data class Success(val message: String = "Login successfully!") : LoginResult()
    data class Error(val message: String) : LoginResult()
}
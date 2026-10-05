package com.example.technical_test_indomarco.domain.repository

import com.example.technical_test_indomarco.domain.model.LoginResult

interface LoginRepository {
    suspend fun login(username: String, password: String): LoginResult
    fun logout()
    fun isLoggedIn(): Boolean
    fun getAuthToken(): String?
}

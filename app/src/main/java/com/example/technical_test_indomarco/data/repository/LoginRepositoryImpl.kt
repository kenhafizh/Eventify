package com.example.technical_test_indomarco.data.repository

import com.example.technical_test_indomarco.data.local.SecureTokenManager
import com.example.technical_test_indomarco.data.remote.api.ApiService
import com.example.technical_test_indomarco.data.remote.dto.LoginRequest
import com.example.technical_test_indomarco.data.remote.dto.LoginResponse
import com.example.technical_test_indomarco.domain.model.LoginResult
import com.example.technical_test_indomarco.domain.repository.LoginRepository
import retrofit2.HttpException
import java.io.IOException

class LoginRepositoryImpl(
    private val primaryApiService: ApiService,
    private val fallbackApiService: ApiService,
    private val tokenManager: SecureTokenManager
) : LoginRepository {

    override suspend fun login(username: String, password: String): LoginResult {
        return try {
            attemptLogin(primaryApiService, username, password)
        } catch (e: IOException) {
            try {
                attemptLogin(fallbackApiService, username, password)
            } catch (e2: IOException) {
                LoginResult.Error("Login error")
            }
        }
    }

    private suspend fun attemptLogin(
        apiService: ApiService,
        username: String,
        password: String
    ): LoginResult {
        val response: LoginResponse
        try {
            response = apiService.login(LoginRequest(username, password))
        } catch (e: HttpException) {
            val message = extractErrorMessage(e)
            return LoginResult.Error(message)
        }
        val message = response.message?.takeIf { it.isNotBlank() } ?: "No response message"
        return if (response.code == "00") {
            tokenManager.saveAuthToken(token = response.code, email = username)
            LoginResult.Success(message)
        } else {
            LoginResult.Error(message)
        }
    }

    private fun extractErrorMessage(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = com.google.gson.Gson().fromJson(errorBody, LoginResponse::class.java)
            errorResponse.message?.takeIf { it.isNotBlank() } ?: "HTTP ${e.code()}: ${e.message()}"
        } catch (_: Exception) {
            "HTTP ${e.code()}: ${e.message()}"
        }
    }

    override fun logout() {
        tokenManager.clearSession()
    }

    override fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    override fun getAuthToken(): String? {
        return tokenManager.getAuthToken()
    }
}

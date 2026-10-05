package com.example.technical_test_indomarco.data.remote.api

import com.example.technical_test_indomarco.data.remote.dto.LoginRequest
import com.example.technical_test_indomarco.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}

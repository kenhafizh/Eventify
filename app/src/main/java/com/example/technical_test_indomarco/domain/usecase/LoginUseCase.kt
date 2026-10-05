package com.example.technical_test_indomarco.domain.usecase

import com.example.technical_test_indomarco.domain.model.LoginResult
import com.example.technical_test_indomarco.domain.repository.LoginRepository

class LoginUseCase(private val repository: LoginRepository) {
    suspend operator fun invoke(username: String, password: String): LoginResult =
        repository.login(username, password)
}

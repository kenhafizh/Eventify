package com.example.technical_test_indomarco.domain.usecase

import com.example.technical_test_indomarco.domain.repository.EventRepository

class GetAllEventsUseCase(private val repository: EventRepository) {
    operator fun invoke() = repository.getAllEvents()
}

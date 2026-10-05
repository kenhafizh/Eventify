package com.example.technical_test_indomarco.domain.usecase

import com.example.technical_test_indomarco.domain.model.Event
import com.example.technical_test_indomarco.domain.repository.EventRepository

class GetEventByIdUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(id: Int): Event? = repository.getEventById(id)
}

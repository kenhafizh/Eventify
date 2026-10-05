package com.example.technical_test_indomarco.domain.repository

import androidx.paging.PagingData
import com.example.technical_test_indomarco.domain.model.Event
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    suspend fun saveEvent(event: Event)
    fun getAllEvents(): Flow<List<Event>>
    fun getEventsPaged(query: String = ""): Flow<PagingData<Event>>
    suspend fun getEventById(id: Int): Event?
}

package com.example.technical_test_indomarco.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.technical_test_indomarco.data.local.dao.EventDao
import com.example.technical_test_indomarco.data.local.entity.EventEntity
import com.example.technical_test_indomarco.domain.model.Event
import com.example.technical_test_indomarco.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepositoryImpl(
    private val eventDao: EventDao
) : EventRepository {

    override suspend fun saveEvent(event: Event) {
        eventDao.insertEvent(event.toEntity())
    }

    override fun getAllEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { list -> list.map { it.toDomain() } }
    }

    override fun getEventsPaged(query: String): Flow<PagingData<Event>> {
        val pagingSource = if (query.isBlank()) {
            eventDao.getEventsPaged()
        } else {
            eventDao.searchEventsPaged(query)
        }
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { pagingSource }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun getEventById(id: Int): Event? {
        return eventDao.getEventById(id)?.toDomain()
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}

// ── Mapper ────────────────────────────────────────────────────────────────────

private fun Event.toEntity() = EventEntity(
    id          = id,
    eventName   = eventName,
    bannerPath  = bannerPath,
    startDate   = startDate,
    startTime   = startTime,
    endDate     = endDate,
    endTime     = endTime,
    location    = location,
    organizer   = organizer,
    description = description,
    createdAt   = createdAt
)

private fun EventEntity.toDomain() = Event(
    id          = id,
    eventName   = eventName,
    bannerPath  = bannerPath,
    startDate   = startDate,
    startTime   = startTime,
    endDate     = endDate,
    endTime     = endTime,
    location    = location,
    organizer   = organizer,
    description = description,
    createdAt   = createdAt
)

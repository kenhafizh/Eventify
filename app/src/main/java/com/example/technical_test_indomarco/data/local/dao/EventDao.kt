package com.example.technical_test_indomarco.data.local.dao

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.technical_test_indomarco.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEventById(id: Int): EventEntity?

    @Query("SELECT * FROM events ORDER BY createdAt DESC")
    fun getEventsPaged(): PagingSource<Int, EventEntity>

    @Query(
        """
        SELECT * FROM events
        WHERE eventName LIKE '%' || :query || '%'
           OR location LIKE '%' || :query || '%'
           OR organizer LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
        """
    )
    fun searchEventsPaged(query: String): PagingSource<Int, EventEntity>
}

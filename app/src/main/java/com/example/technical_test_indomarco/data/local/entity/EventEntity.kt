package com.example.technical_test_indomarco.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventName: String,
    val bannerPath: String,
    val startDate: String,
    val startTime: String,
    val endDate: String,
    val endTime: String,
    val location: String,
    val organizer: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)

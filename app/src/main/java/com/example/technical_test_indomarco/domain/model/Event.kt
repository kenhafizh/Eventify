package com.example.technical_test_indomarco.domain.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class Event(
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
) {

    val startDateTimeMillis: Long by lazy(LazyThreadSafetyMode.NONE) {
        if (startDate.isBlank()) 0L else parseDateTime(startDate, startTime)
    }

    val endDateTimeMillis: Long by lazy(LazyThreadSafetyMode.NONE) {
        val effectiveEndDate = if (endDate.isNotBlank()) endDate else startDate
        val effectiveEndTime = if (endTime.isNotBlank()) endTime else if (startTime.isNotBlank()) startTime else ""
        if (effectiveEndDate.isBlank()) return@lazy startDateTimeMillis
        if (effectiveEndTime.isNotBlank()) {
            val parsed = parseDateTime(effectiveEndDate, effectiveEndTime)
            if (parsed != 0L) return@lazy parsed
        }
        parseEndOfDay(effectiveEndDate) ?: startDateTimeMillis
    }

    private fun parseDateTime(date: String, time: String): Long {
        if (time.isNotBlank()) {
            parseDateTimeFormat(date, time, Locale.getDefault())?.let { return it }
            parseDateTimeFormat(date, time, Locale.US)?.let { return it }
        }
        parseDateFormat(date, Locale.getDefault())?.let { return it }
        parseDateFormat(date, Locale.US)?.let { return it }
        return 0L
    }

    private fun parseDateTimeFormat(date: String, time: String, locale: Locale): Long? {
        return try {
            val format = SimpleDateFormat("MMM dd, yyyy hh:mm a", locale)
            format.parse("$date $time")?.time
        } catch (_: Exception) {
            null
        }
    }

    private fun parseDateFormat(date: String, locale: Locale): Long? {
        return try {
            val format = SimpleDateFormat("MMM dd, yyyy", locale)
            format.parse(date)?.time
        } catch (_: Exception) {
            null
        }
    }

    private fun parseEndOfDay(date: String): Long? {
        return try {
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val parsed = format.parse(date)
                ?: SimpleDateFormat("MMM dd, yyyy", Locale.US).parse(date)
                ?: return null
            val cal = Calendar.getInstance().apply {
                time = parsed
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            cal.timeInMillis
        } catch (_: Exception) {
            null
        }
    }
}

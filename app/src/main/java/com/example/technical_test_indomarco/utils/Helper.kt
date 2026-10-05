package com.example.technical_test_indomarco.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.technical_test_indomarco.domain.model.Event
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Helper {

    val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val timeFormat: SimpleDateFormat
        get() = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun loadBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        if (uri == null) return null
        return try {
            if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun showStartDatePicker(
        context: Context,
        startDate: String,
        endDate: String,
        onStartDateSelected: (String) -> Unit,
        onResetEndDate: () -> Unit = {}
    ) {
        val cal = Calendar.getInstance()
        if (startDate.isNotBlank()) {
            try {
                dateFormat.parse(startDate)?.let { cal.time = it }
            } catch (_: Exception) {}
        }
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val formattedDate = dateFormat.format(selectedCal.time)
                onStartDateSelected(formattedDate)

                // If endDate is already selected and is before the new startDate, reset endDate
                if (endDate.isNotBlank()) {
                    try {
                        dateFormat.parse(endDate)?.let { endParsed ->
                            val endCal = Calendar.getInstance().apply {
                                time = endParsed
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            if (endCal.before(selectedCal)) {
                                onResetEndDate()
                            }
                        }
                    } catch (_: Exception) {}
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = todayCal.timeInMillis - 1000
        dialog.show()
    }

    fun showEndDatePicker(
        context: Context,
        startDate: String,
        endDate: String,
        onEndDateSelected: (String) -> Unit
    ) {
        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val minCal = Calendar.getInstance().apply {
            timeInMillis = todayCal.timeInMillis
        }
        if (startDate.isNotBlank()) {
            try {
                dateFormat.parse(startDate)?.let { parsedStart ->
                    val startCal = Calendar.getInstance().apply {
                        time = parsedStart
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    if (startCal.timeInMillis > minCal.timeInMillis) {
                        minCal.timeInMillis = startCal.timeInMillis
                    }
                }
            } catch (_: Exception) {}
        }

        val initialCal = Calendar.getInstance()
        if (endDate.isNotBlank()) {
            try {
                dateFormat.parse(endDate)?.let { initialCal.time = it }
            } catch (_: Exception) {
                initialCal.timeInMillis = minCal.timeInMillis
            }
        } else {
            initialCal.timeInMillis = minCal.timeInMillis
        }
        if (initialCal.before(minCal)) {
            initialCal.timeInMillis = minCal.timeInMillis
        }

        val dialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                onEndDateSelected(dateFormat.format(selectedCal.time))
            },
            initialCal.get(Calendar.YEAR),
            initialCal.get(Calendar.MONTH),
            initialCal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = minCal.timeInMillis - 1000
        dialog.show()
    }

    fun showTimePicker(
        context: Context,
        currentTime: String,
        onTimeSelected: (String) -> Unit
    ) {
        val cal = Calendar.getInstance()
        if (currentTime.isNotBlank()) {
            try {
                timeFormat.parse(currentTime)?.let { cal.time = it }
            } catch (_: Exception) {}
        }
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val selected = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                onTimeSelected(timeFormat.format(selected.time))
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
    }


    fun separateEvents(events: List<Event>): Pair<Event?, List<Event>> {
        if (events.isEmpty()) return Pair(null, emptyList())

        val now = System.currentTimeMillis()

        val futureEvents = events.filter { it.startDateTimeMillis >= now }
        val ongoingEvents = events.filter {
            it.startDateTimeMillis < now && it.endDateTimeMillis >= now
        }

        val closestUpcoming: Event? = when {
            futureEvents.isNotEmpty() -> {
                futureEvents.minWithOrNull(
                    compareBy<Event> { it.startDateTimeMillis }
                        .thenByDescending { it.createdAt }
                )
            }
            ongoingEvents.isNotEmpty() -> {
                ongoingEvents.maxByOrNull { it.createdAt }
            }
            else -> null
        }

        val otherEvents = events
            .filter { it.id != closestUpcoming?.id }
            .sortedByDescending { it.createdAt }

        return Pair(closestUpcoming, otherEvents)
    }
}

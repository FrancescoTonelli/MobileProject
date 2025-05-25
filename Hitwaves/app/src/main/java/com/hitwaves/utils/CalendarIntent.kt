package com.hitwaves.utils

import android.content.Intent
import android.provider.CalendarContract
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun createCalendarIntent(
    eventName: String,
    date: String,
    time: String,
    location: String
): Intent? {
    return try {
        val datetimeString = "$date $time"

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val startDate = sdf.parse(datetimeString) ?: return null

        val startCalendar = Calendar.getInstance().apply {
            setTime(startDate)
        }

        val endCalendar = Calendar.getInstance().apply {
            setTime(startDate)
            add(Calendar.HOUR_OF_DAY, 2)
        }

        Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, eventName)
            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startCalendar.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCalendar.timeInMillis)
            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        }

    } catch (e: Exception) {
        null
    }
}

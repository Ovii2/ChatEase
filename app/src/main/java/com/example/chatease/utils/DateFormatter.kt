package com.example.chatease.utils

import android.content.Context
import com.example.chatease.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toChatTimeStamp(): String {
    val localDateTime = getLocalDateTime()

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dayFormatter = DateTimeFormatter.ofPattern("EEE")

    val messageDate = localDateTime.toLocalDate()
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM")

    return when {
        messageDate == today -> {
            localDateTime.format(timeFormatter)
        }

        messageDate == today.minusDays(1) -> {
            "Yesterday"
        }

        messageDate.isAfter(today.minusDays(7)) -> {
            localDateTime.format(dayFormatter)
        }

        else -> {
            localDateTime.format(dateFormatter)
        }
    }
}

fun Long.toFormattedTime(): String {
    val totalMinutes = this / (1000 * 60)

    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    return "%02d:%02d".format(hours, minutes)
}

fun isSameDay(firstTimestamp: Long, secondTimestamp: Long): Boolean {
    val firstDate = Instant
        .ofEpochMilli(firstTimestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .toLocalDate()

    val secondDate = Instant
        .ofEpochMilli(secondTimestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .toLocalDate()

    return firstDate == secondDate
}

fun Long.toChatDateLabel(context: Context): String {
    val localDateTime = getLocalDateTime()

    val messageDate = localDateTime.toLocalDate()
    val today = LocalDate.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy MMM dd")

    return when (messageDate) {
        today -> context.getString(R.string.today)
        today.minusDays(1) -> context.getString(R.string.yesterday)
        else -> localDateTime.format(dateFormatter)
    }
}

fun Long.toChatBubbleTimeStamp(): String {
    val localDateTime = getLocalDateTime()

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    return localDateTime.format(timeFormatter)
}

private fun Long.getLocalDateTime(): LocalDateTime = Instant
    .ofEpochMilli(this)
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime()



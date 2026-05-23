package com.example.chatease.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toChatTimeStamp(): String {
    val localDateTime = Instant
        .ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

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

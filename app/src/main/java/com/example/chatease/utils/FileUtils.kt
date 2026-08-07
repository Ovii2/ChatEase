package com.example.chatease.utils

fun String.toTruncatedFileName(): String {
    if (this.length >= 35) {
        return this.take(20) + "..." + this.takeLast(12)
    }
    return this
}
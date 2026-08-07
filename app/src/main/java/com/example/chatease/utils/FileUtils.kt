package com.example.chatease.utils

fun String.toTruncatedFileName(): String {
    if (this.length >= 50) {
        return this.take(20) + "..." + this.takeLast(20)
    }
    return this
}
package com.example.chatease.presentation.validation

import android.util.Patterns
import com.example.chatease.R

object AuthValidator {

    fun validateEmail(email: String): Int? {
        if (email.isBlank()) {
            return R.string.error_email_empty
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return R.string.error_email_format
        }
        return null
    }

    fun validatePassword(password: String): Int? {
        if (password.isBlank()) {
            return R.string.error_password_empty
        }
        return null
    }

    fun validateFullName(fullName: String): Int? {
        if (fullName.isBlank()) {
            return R.string.error_full_name_empty
        }

        if (fullName.length < 2) {
            return R.string.error_full_name_length
        }

        if (!fullName.matches(Regex("^[\\p{L}\\s'-]+$"))) {
            return R.string.error_full_name_invalid
        }
        return null
    }

    fun validateSignUpPassword(password: String): Int? {
        if (password.isBlank()) {
            return R.string.error_password_empty
        }

        if (password.length < 6) {
            return R.string.error_password_length
        }
        return null
    }

    fun validateSignUpConfirmPassword(password: String, confirmPassword: String): Int? {
        if (confirmPassword.isBlank()) {
            return R.string.error_confirm_password_empty
        }

        if (password != confirmPassword) {
            return R.string.error_confirm_password_match
        }
        return null
    }
}
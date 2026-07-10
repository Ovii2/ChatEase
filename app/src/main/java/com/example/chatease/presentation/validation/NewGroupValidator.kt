package com.example.chatease.presentation.validation

object NewGroupValidator {

    fun validateNewGroupName(groupName: String): Boolean {
        return groupName.isBlank() || groupName.length < 5 || groupName.length > 50
    }
}
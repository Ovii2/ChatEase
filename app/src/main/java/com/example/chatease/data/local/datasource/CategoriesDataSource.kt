package com.example.chatease.data.local.datasource

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.WorkOutline
import com.example.chatease.R
import com.example.chatease.domain.model.Category

object CategoriesDataSource {

    val categories = listOf(
        Category(
            id = "all",
            name = R.string.all,
            icon = Icons.Outlined.GridView,
        ),
        Category(
            id = "friends",
            name = R.string.friends,
            icon = Icons.Outlined.Group,
        ),
        Category(
            id = "family",
            name = R.string.family,
            icon = Icons.Outlined.Home,
        ),
        Category(
            id = "work",
            name = R.string.work,
            icon = Icons.Outlined.WorkOutline,
        ),
        Category(
            id = "gaming",
            name = R.string.gaming,
            icon = Icons.Outlined.SportsEsports,
        ),
        Category(
            id = "others",
            name = R.string.others,
            icon = Icons.Outlined.Category,
        ),
    )
}

package com.example.chatease.presentation.ui.screens.shared.panes.left_pane.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Category
import com.example.chatease.presentation.ui.screens.shared.chat.CommonChip

@Composable
fun CategoriesRow(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    LazyRow(modifier = modifier.padding(4.dp)) {
        items(categories) { category ->
            CommonChip(
                text = category.name,
                selected = category.name == selectedCategory,
                onClick = { onSelectCategory(category.name) }
            )
        }
    }
}

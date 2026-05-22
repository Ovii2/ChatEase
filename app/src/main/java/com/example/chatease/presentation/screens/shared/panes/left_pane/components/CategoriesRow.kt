package com.example.chatease.presentation.screens.shared.panes.left_pane.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.Category
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun CategoriesRow(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    LazyRow(modifier = modifier.padding(4.dp)) {
        items(categories) { category ->
            CategoryChip(
                category = category,
                selected = category.name == selectedCategory,
                onSelectCategory = onSelectCategory
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: Category,
    selected: Boolean,
    onSelectCategory: (String) -> Unit
) {
    FilterChip(
        modifier = Modifier.padding(end = 8.dp),
        selected = selected,
        onClick = { onSelectCategory(category.name) },
        label = {
            Text(
                text = category.name,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            labelColor = MaterialTheme.colorScheme.onBackground,
            selectedLabelColor = MaterialTheme.colorScheme.surface
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderWidth = 1.5.dp,
            borderWidth = 0.dp,
            borderColor = Color.Transparent,
            selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        shape = CircleShape
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CategoryChipPreview() {
    val category = Category(name = "Work")
    ChatEaseTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CategoryChip(
                category = category,
                selected = true,
                onSelectCategory = {}
            )
        }
    }
}
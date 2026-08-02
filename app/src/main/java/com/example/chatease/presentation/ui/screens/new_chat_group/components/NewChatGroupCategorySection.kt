package com.example.chatease.presentation.ui.screens.new_chat_group.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.data.local.datasource.CategoriesDataSource
import com.example.chatease.domain.model.Category
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun NewChatGroupCategorySection(
    modifier: Modifier = Modifier,
    selectedCategoryId: String?,
    onCategorySelect: (String) -> Unit
) {
    val categories = CategoriesDataSource.categories

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.categories),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.W600
        )
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.drop(1)) { category ->
                NewChatGroupCategoryItem(
                    category = category,
                    selected = selectedCategoryId == category.id,
                    onClick = {
                        onCategorySelect(category.id)
                    },
                )
            }
        }
    }
}


@Composable
fun NewChatGroupCategoryItem(
    modifier: Modifier = Modifier,
    category: Category,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val shape = RoundedCornerShape(15.dp)

    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(
                color = backgroundColor,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                shape = shape
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        Icon(
            imageVector = category.icon,
            contentDescription = null,
            tint = color
        )
        Text(
            text = stringResource(category.name),
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewChatGroupCategoryItemPreview() {
    val category = Category(
        id = "1",
        name = R.string.friends,
        icon = Icons.Outlined.Group
    )
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NewChatGroupCategoryItem(
                    category = category,
                    selected = true,
                    onClick = {},
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NewChatGroupCategorySectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NewChatGroupCategorySection(
                    selectedCategoryId = "2",
                    onCategorySelect = {}
                )
            }
        }
    }
}

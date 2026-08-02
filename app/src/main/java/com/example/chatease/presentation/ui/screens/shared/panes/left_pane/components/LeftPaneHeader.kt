package com.example.chatease.presentation.ui.screens.shared.panes.left_pane.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.chatease.R
import com.example.chatease.domain.model.Category
import com.example.chatease.presentation.ui.screens.shared.chat.ChatSearchBar
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun LeftPaneHeader(
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    categories: List<Category>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    var expanded by rememberSaveable() { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row() {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null
                )
                Box(
                    modifier = Modifier
                        .clickable { expanded = true }
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUrl == null) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Outlined.PersonOutline,
                            contentDescription = null
                        )
                    } else {
                        AsyncImage(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            model = imageUrl,
                            contentDescription = null
                        )
//                        Image(
//                            modifier = Modifier
//                                .size(40.dp)
//                                .clip(CircleShape),
//                            contentScale = ContentScale.Crop,
//                            painter = painterResource(R.drawable.person),
//                            contentDescription = null
//                        )
                    }
                    ProfileDropdown(
                        expanded = expanded,
                        onDismiss = { expanded = false },
                        onProfileClick = onNavigateToProfile,
                        onLogoutClick = { onLogoutClick() },
                        offset = DpOffset(
                            x = 0.dp,
                            y = 8.dp
                        )
                    )
                }
            }
        }
        ChatSearchBar(
            value = "",
            onValueChange = {},
            onClearSearch = {},
            placeholder = R.string.search_conversations
        )
        CategoriesRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onSelectCategory = onSelectCategory
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LeftPaneHeaderPreview() {
    val categories = List(10) {
        Category(
            id = it.toString(),
            name = R.string.friends,
            icon = Icons.Outlined.Group
        )
    }

    ChatEaseTheme {
        Column(modifier = Modifier.systemBarsPadding()) {
            LeftPaneHeader(
                imageUrl = null,
                categories = categories,
                selectedCategory = "All",
                onSelectCategory = {},
                onLogoutClick = {},
                onNavigateToProfile = {},
            )
        }
    }
}

package com.example.chatease.presentation.ui.screens.membership.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MembershipCardTopSection(
    modifier: Modifier = Modifier,
    membership: Membership,
    isDiscounted: Boolean = false,
    backgroundTintColor: Color
) {
    val icon = when (membership) {
        Membership.FREE -> R.drawable.outline_nest_eco_leaf_24
        Membership.PREMIUM -> R.drawable.outline_diamond_24
        Membership.ULTRA -> R.drawable.outline_crown_24
    }

    val title = when (membership) {
        Membership.FREE -> R.string.free_title
        Membership.PREMIUM -> R.string.premium_title
        Membership.ULTRA -> R.string.ultra_title
    }

    val label = when (membership) {
        Membership.FREE -> R.string.free_label
        Membership.PREMIUM -> R.string.premium_label
        Membership.ULTRA -> R.string.ultra_label
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    color = backgroundTintColor,
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.W600
        )
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.W500,
            color = if (isDiscounted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MembershipCardTopSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MembershipCardTopSection(
                    membership = Membership.PREMIUM,
                    isDiscounted = false,
                    backgroundTintColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                )
            }
        }
    }
}
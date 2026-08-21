package com.example.chatease.presentation.ui.screens.membership.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.presentation.ui.screens.membership.components.card.MembershipScreenCard
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MembershipScreenBody(
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        with(density) {
            listState.scrollToItem(
                index = 1,
                scrollOffset = (-70).dp.roundToPx()
            )
        }
    }

    LazyRow(
        modifier = modifier,
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MembershipScreenCard(
                modifier = Modifier,
                membership = Membership.FREE
            )
        }
        item {
            MembershipScreenCard(
                modifier = Modifier,
                membership = Membership.PREMIUM,
                isRecommended = true,
                isDiscounted = true,
                discount = 25.0
            )
        }
        item {
            MembershipScreenCard(
                modifier = Modifier,
                membership = Membership.ULTRA,
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MembershipScreenBodyPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MembershipScreenBody()
            }
        }
    }
}
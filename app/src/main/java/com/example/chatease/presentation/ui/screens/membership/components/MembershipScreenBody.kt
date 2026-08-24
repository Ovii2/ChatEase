package com.example.chatease.presentation.ui.screens.membership.components

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.presentation.ui.screens.membership.components.card.MembershipScreenCard
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MembershipScreenBody(
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 1)
    val density = LocalDensity.current
    val activity = LocalActivity.current
    val widthSizeClass = activity?.let {
        calculateWindowSizeClass(it).widthSizeClass
    }

    var selectedMembership by rememberSaveable { mutableStateOf(Membership.PREMIUM) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val cardWidth = when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> 280.dp
            WindowWidthSizeClass.Medium -> 300.dp
            else -> 320.dp
        }
        val centerOffset = (maxWidth - cardWidth) / 2

        LaunchedEffect(Unit) {
            with(density) {
                listState.scrollToItem(
                    index = 1,
                    scrollOffset = -centerOffset.roundToPx()
                )
            }
        }

        LazyRow(
            modifier = Modifier,
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                MembershipScreenCard(
                    modifier = Modifier,
                    membership = Membership.FREE,
                    cardWidth = cardWidth,
                    isSelected = selectedMembership == Membership.FREE,
                    onClick = { selectedMembership = Membership.FREE },
                )
            }
            item {
                MembershipScreenCard(
                    modifier = Modifier,
                    membership = Membership.PREMIUM,
                    isRecommended = true,
                    isDiscounted = true,
                    discount = 25.0,
                    cardWidth = cardWidth,
                    isSelected = selectedMembership == Membership.PREMIUM,
                    onClick = { selectedMembership = Membership.PREMIUM },
                )
            }
            item {
                MembershipScreenCard(
                    modifier = Modifier,
                    membership = Membership.ULTRA,
                    cardWidth = cardWidth,
                    isSelected = selectedMembership == Membership.ULTRA,
                    onClick = { selectedMembership = Membership.ULTRA }
                )
            }
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
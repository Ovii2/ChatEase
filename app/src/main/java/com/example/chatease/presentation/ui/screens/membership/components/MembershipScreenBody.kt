package com.example.chatease.presentation.ui.screens.membership.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.data.local.datasource.MembershipBenefitsDataSource
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.presentation.ui.screens.membership.components.card.MembershipScreenCard
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MembershipScreenBody(
    modifier: Modifier = Modifier,
    isRecommended: Boolean = false
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MembershipScreenCard(
            modifier = Modifier,
            isRecommended = isRecommended,
            membership = Membership.FREE
        )

        MembershipScreenCard(
            modifier = Modifier,
            membership = Membership.PREMIUM,
            isRecommended = true,
            isDiscounted = true,
            discount = 25.0
        )

        MembershipScreenCard(
            modifier = Modifier,
            membership = Membership.ULTRA,
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MembershipScreenBodyPreview() {
    val benefitItems = MembershipBenefitsDataSource.getBenefits(Membership.PREMIUM)
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MembershipScreenBody(
                    isRecommended = false,
                )
            }
        }
    }
}
package com.example.chatease.presentation.ui.screens.membership.components.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.chatease.data.local.datasource.MembershipBenefitsDataSource
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MembershipScreenCard(
    modifier: Modifier = Modifier,
    isRecommended: Boolean = false,
    isDiscounted: Boolean = false,
    membership: Membership,
    discount: Double = 0.0,
    cardWidth: Dp = 280.dp
) {
    val backgroundTintColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    var isSelected by rememberSaveable { mutableStateOf(false) }

    val benefitItems = when (membership) {
        Membership.FREE -> MembershipBenefitsDataSource.getBenefits(Membership.FREE)
        Membership.PREMIUM -> MembershipBenefitsDataSource.getBenefits(Membership.PREMIUM)
        Membership.ULTRA -> MembershipBenefitsDataSource.getBenefits(Membership.ULTRA)
    }

    Box {
        if (isRecommended) {
            MembershipCardRecommendedBadge(
                modifier = modifier.align(Alignment.TopCenter),
                offsetY = (-20).dp,
            )
        }
        Surface(
            modifier = Modifier
                .widthIn(max = cardWidth)
                .heightIn(min = 520.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    isSelected = !isSelected
                },
            color = Color.Transparent,
            shape = RoundedCornerShape(10.dp),
            border =
                BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                    }
                )
        ) {
            Column(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            if (isRecommended) {
                                listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    MaterialTheme.colorScheme.surface,
                                )
                            } else listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .padding(
                        vertical = 24.dp,
                        horizontal = 8.dp
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MembershipCardTopSection(
                    membership = membership,
                    isDiscounted = isDiscounted,
                    backgroundTintColor = backgroundTintColor,
                )
                MembershipCardPricingSection(
                    membership = membership,
                    isDiscounted = isDiscounted,
                    discount = discount,
                    backgroundTintColor = backgroundTintColor,
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(
                            top = if (!isRecommended) 32.dp else 0.dp,
                            bottom = 8.dp
                        ),
                    thickness = 1.dp,
                    color = if (isRecommended) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                )
                MembershipScreenBenefitsSection(
                    benefitItems = benefitItems
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MembershipScreenCardPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MembershipScreenCard(
                    membership = Membership.FREE
                )
                MembershipScreenCard(
                    membership = Membership.PREMIUM,
                    isRecommended = true,
                    isDiscounted = true,
                    discount = 25.0
                )
                MembershipScreenCard(
                    membership = Membership.ULTRA
                )
            }
        }
    }
}

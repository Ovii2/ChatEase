package com.example.chatease.presentation.ui.screens.membership.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.presentation.ui.theme.ChatEaseTheme
import kotlin.math.round

@Composable
fun MembershipCardPricingSection(
    modifier: Modifier = Modifier,
    membership: Membership,
    isDiscounted: Boolean = false,
    discount: Double = 0.0,
    backgroundTintColor: Color
) {
    val price = when (membership) {
        Membership.FREE -> 0.00
        Membership.PREMIUM -> 9.99
        Membership.ULTRA -> 19.99
    }

    val duration = when (membership) {
        Membership.FREE -> R.string.free_duration
        else -> R.string.premium_duration
    }

    val discountedPrice = round(price * ((100 - discount) / 100) * 100) / 100.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isDiscounted && discount in 1.0..100.0) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 85.dp, height = 30.dp)
                        .background(
                            color = backgroundTintColor,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.discount_off, discount),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.W500
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "€$discountedPrice",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = " / ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        modifier = Modifier.offset(y = (3).dp),
                        text = "€${price}",
                        textDecoration = TextDecoration.LineThrough,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

        } else {
            val formattedPrice = if (price == 0.0) "€0" else "€%.2f".format(price)

            Text(
                text = formattedPrice,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.W600
            )
        }
        Text(
            text = stringResource(duration),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MembershipCardPricingSectionPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MembershipCardPricingSection(
                    membership = Membership.ULTRA,
                    isDiscounted = true,
                    discount = 25.0,
                    backgroundTintColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }
        }
    }
}
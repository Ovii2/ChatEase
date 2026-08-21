package com.example.chatease.presentation.ui.screens.membership.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.domain.model.MembershipBenefitItem
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MembershipScreenBenefitsSection(
    modifier: Modifier = Modifier,
    benefitItems: List<MembershipBenefitItem>
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            benefitItems.forEach { item ->
                MembershipScreenCardBenefitItem(item = item)
            }
        }
    }
}

@Composable
fun MembershipScreenCardBenefitItem(
    modifier: Modifier = Modifier,
    item: MembershipBenefitItem
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircleOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(item.text),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MembershipScreenBenefitsSectionPreview() {
    val items = List(10) {
        MembershipBenefitItem(
            text = R.string.premium_benefit_1
        )
    }
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MembershipScreenBenefitsSection(
                    benefitItems = items
                )
            }
        }
    }
}
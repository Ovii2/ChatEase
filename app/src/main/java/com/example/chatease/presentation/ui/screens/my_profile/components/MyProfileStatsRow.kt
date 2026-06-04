package com.example.chatease.presentation.ui.screens.my_profile.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.chatease.presentation.ui.model.ProfileStatUiModel
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MyProfileStatsRow(
    modifier: Modifier = Modifier,
    stats: List<ProfileStatUiModel>
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        stats.forEach {
            MyProfileStatsItem(
                modifier = Modifier.weight(1f),
                profileStat = it
            )
        }
    }
}

@Composable
fun MyProfileStatsItem(
    modifier: Modifier = Modifier,
    profileStat: ProfileStatUiModel
) {
    Card(
        modifier = modifier
            .height(90.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = profileStat.value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(profileStat.label),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MyProfileStatsRowPreview() {
    val stats = List(3) {
        ProfileStatUiModel(
            value = it.toString(),
            label = R.string.chats
        )
    }
    ChatEaseTheme {
        Scaffold {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                MyProfileStatsRow(
                    stats = stats
                )
            }
        }
    }
}

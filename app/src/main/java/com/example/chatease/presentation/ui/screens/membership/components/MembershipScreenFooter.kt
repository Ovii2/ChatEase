package com.example.chatease.presentation.ui.screens.membership.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chatease.R
import com.example.chatease.presentation.ui.theme.ChatEaseTheme

@Composable
fun MembershipScreenFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.VerifiedUser,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.cancel_any_time),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.W500
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.secure_payments),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.W500
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {},
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(text = stringResource(R.string.continue_btn))
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = {},
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(text = stringResource(R.string.restore_purchase))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MembershipScreenFooterPreview() {
    ChatEaseTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MembershipScreenFooter()
            }
        }
    }
}

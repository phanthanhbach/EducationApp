package com.example.educationapp.core.ui.shimmer.skeleton

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.shimmer.ShimmerBox
import com.example.educationapp.core.ui.shimmer.ShimmerLine

@Composable
fun DashboardSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AppDimen.p16)
    ) {
        // 1. Top summary/rating card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                AppDimen.p1,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(AppDimen.p16)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimen.p16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBox(
                    width = AppDimen.p64,
                    height = AppDimen.p64,
                    shape = RoundedCornerShape(AppDimen.p12)
                )
                Spacer(modifier = Modifier.width(AppDimen.p16))
                Column(verticalArrangement = Arrangement.spacedBy(AppDimen.p8)) {
                    ShimmerLine(width = 120.dp, height = 18.dp)
                    ShimmerLine(width = 80.dp, height = 14.dp)
                }
            }
        }

        // 2. Upcoming Schedule Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                AppDimen.p1,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(AppDimen.p16)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimen.p16),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
            ) {
                ShimmerLine(width = 140.dp, height = AppDimen.p16)
                repeat(2) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ShimmerBox(
                            width = 40.dp,
                            height = 40.dp,
                            shape = RoundedCornerShape(AppDimen.p8)
                        )
                        Spacer(modifier = Modifier.width(AppDimen.p12))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            ShimmerLine(width = 160.dp, height = 14.dp)
                            ShimmerLine(width = 100.dp, height = 10.dp)
                        }
                    }
                }
            }
        }

        // 3. Bottom list section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(AppDimen.p16)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppDimen.p16),
                verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
            ) {
                ShimmerLine(width = AppDimen.p100, height = AppDimen.p16)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.p12)
                ) {
                    repeat(2) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(AppDimen.p8)
                        ) {
                            ShimmerLine(width = 60.dp, height = AppDimen.p12)
                            ShimmerLine(width = 40.dp, height = 10.dp)
                        }
                    }
                }
            }
        }
    }
}

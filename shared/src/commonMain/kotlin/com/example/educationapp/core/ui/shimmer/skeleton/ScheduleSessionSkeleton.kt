package com.example.educationapp.core.ui.shimmer.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.educationapp.core.ui.shimmer.ShimmerLine

@Composable
fun ScheduleSessionSkeleton(
    modifier: Modifier = Modifier,
    sessionCount: Int = 3,
    showDateHeader: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showDateHeader) {
            ShimmerLine(
                width = 100.dp,
                height = 18.dp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        repeat(sessionCount) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ShimmerLine(
                            width = 60.dp,
                            height = 14.dp
                        )
                        ShimmerLine(
                            width = 40.dp,
                            height = 12.dp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShimmerLine(
                            width = 180.dp,
                            height = 16.dp
                        )
                        ShimmerLine(
                            width = 120.dp,
                            height = 12.dp
                        )
                    }
                }
            }
        }
    }
}

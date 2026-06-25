package com.example.educationapp.core.ui.shimmer.skeleton

import androidx.compose.foundation.BorderStroke
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
import com.example.educationapp.core.ui.shimmer.ShimmerBox
import com.example.educationapp.core.ui.shimmer.ShimmerLine

@Composable
fun AssignmentCardSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 3
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    ShimmerBox(
                        width = 40.dp,
                        height = 40.dp,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Title row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ShimmerLine(width = 140.dp, height = 16.dp)
                            ShimmerBox(width = 70.dp, height = 18.dp, shape = RoundedCornerShape(6.dp))
                        }

                        // Description line
                        ShimmerLine(width = 200.dp, height = 13.dp)

                        // Due date line
                        ShimmerLine(width = 120.dp, height = 12.dp)

                        // Badges Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ShimmerBox(width = 90.dp, height = 18.dp, shape = RoundedCornerShape(6.dp))
                            ShimmerBox(width = 70.dp, height = 18.dp, shape = RoundedCornerShape(6.dp))
                        }

                        // Actions Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ShimmerLine(width = 80.dp, height = 14.dp)
                            ShimmerBox(width = 90.dp, height = 30.dp, shape = RoundedCornerShape(8.dp))
                        }
                    }
                }
            }
        }
    }
}

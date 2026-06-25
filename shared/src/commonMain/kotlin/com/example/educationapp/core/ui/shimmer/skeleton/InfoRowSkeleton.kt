package com.example.educationapp.core.ui.shimmer.skeleton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.ui.shimmer.ShimmerCircle
import com.example.educationapp.core.ui.shimmer.ShimmerLine

@Composable
fun InfoRowSkeleton(
    modifier: Modifier = Modifier,
    rowCount: Int = 5,
    showSectionTitle: Boolean = false,
    showIcons: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showSectionTitle) {
            ShimmerLine(
                width = 120.dp,
                height = 20.dp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        repeat(rowCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showIcons) {
                    ShimmerCircle(size = 24.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ShimmerLine(
                        width = 80.dp,
                        height = 12.dp
                    )
                    ShimmerLine(
                        width = 180.dp,
                        height = 16.dp
                    )
                }
            }
        }
    }
}

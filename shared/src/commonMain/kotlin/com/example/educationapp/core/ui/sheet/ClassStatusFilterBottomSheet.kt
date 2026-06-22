package com.example.educationapp.core.ui.sheet

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.chip.AppChip
import com.example.educationapp.core.ui.text.AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassStatusFilterBottomSheet(
    selectedStatus: String?,
    statuses: List<Pair<String?, String>>,
    onStatusSelect: (String?) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tempSelectedStatus by remember(selectedStatus) { mutableStateOf(selectedStatus) }

    AppBottomSheet(
        onDismissRequest = onDismissRequest,
        title = "Lọc trạng thái lớp học",
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimen.p24)
                .padding(bottom = AppDimen.p24, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statuses.forEach { (statusKey, statusLabel) ->
                    val isSelected = tempSelectedStatus == statusKey
                    AppChip(
                        text = statusLabel,
                        selected = isSelected,
                        onClick = { tempSelectedStatus = statusKey }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onStatusSelect(tempSelectedStatus)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                AppText(
                    text = "Xác nhận",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

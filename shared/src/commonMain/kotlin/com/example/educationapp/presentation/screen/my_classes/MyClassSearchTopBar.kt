package com.example.educationapp.presentation.screen.my_classes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.text.AppText
import com.example.educationapp.core.ui.textfield.SearchTextField
import com.example.educationapp.core.ui.textfield.SearchTrigger
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.search_placeholder
import org.jetbrains.compose.resources.stringResource

/**
 * Thanh tiêu đề tích hợp tìm kiếm thích ứng (Responsive Search Top Bar) dành riêng cho AssignmentTab.
 * Tự động thay đổi bố cục tùy theo chiều rộng màn hình:
 * - Mobile (dọc, < 600.dp): Sắp xếp Title -> Search Bar -> Filter Chips theo chiều dọc.
 * - Tablet/Landscape (ngang, >= 600.dp): Title và Search Bar đặt chung trên một hàng ngang, Filter Chips nằm bên dưới.
 */
@Composable
fun SearchTopBar(
    title: String,
    searchQuery: String,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(Res.string.search_placeholder),
    searchTrigger: SearchTrigger = SearchTrigger.DEBOUNCE,
    containerColor: Color = Color.White,
    filterContent: @Composable (() -> Unit)? = null
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .statusBarsPadding()
    ) {
        val isTabletOrLandscape = maxWidth >= 600.dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppDimen.p16, vertical = AppDimen.p12),
            verticalArrangement = Arrangement.spacedBy(AppDimen.p12)
        ) {
            if (isTabletOrLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppText(
                        text = title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(AppDimen.p16))
                    SearchTextField(
                        value = searchQuery,
                        onSearch = onSearch,
                        placeholder = placeholder,
                        searchTrigger = searchTrigger,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.p8)
                ) {
                    AppText(
                        text = title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    SearchTextField(
                        value = searchQuery,
                        onSearch = onSearch,
                        placeholder = placeholder,
                        searchTrigger = searchTrigger,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (filterContent != null) {
                filterContent()
            }
        }
    }
}

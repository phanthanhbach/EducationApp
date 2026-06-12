package com.example.educationapp.core.ui.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.icon.AppIcon
import educationapp.shared.generated.resources.Res
import educationapp.shared.generated.resources.ic_close_24dp
import educationapp.shared.generated.resources.ic_search_24dp
import educationapp.shared.generated.resources.search_placeholder
import org.jetbrains.compose.resources.stringResource
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Các chế độ kích hoạt tìm kiếm của [SearchTextField].
 */
enum class SearchTrigger {
    /** Tìm kiếm ngay lập tức trên mỗi ký tự nhập vào. */
    IMMEDIATE,

    /** Tự động tìm kiếm sau khi người dùng dừng nhập một khoảng thời gian [debounceMs]. */
    DEBOUNCE,

    /** Chỉ tìm kiếm khi bấm nút tìm kiếm trên bàn phím hoặc nút xác nhận. */
    CONFIRM
}

/**
 * Component ô tìm kiếm (Search Bar) tái sử dụng cho toàn bộ ứng dụng.
 * Hỗ trợ các hành vi tìm kiếm khác nhau (immediate, debounce, confirm) và tự động đồng bộ
 * khi giá trị tìm kiếm từ bên ngoài thay đổi.
 *
 * @param value Giá trị hiện tại của từ khóa tìm kiếm được đồng bộ từ bên ngoài.
 * @param onSearch Callback được gọi khi hành động tìm kiếm được thực hiện (dựa trên [searchTrigger]).
 * @param modifier Modifier tùy biến layout.
 * @param onValueChange Callback tùy chọn khi chữ trong ô nhập thay đổi (trước khi trigger tìm kiếm).
 * @param placeholder Gợi ý hiển thị trong ô nhập liệu.
 * @param debounceMs Thời gian chờ (milli-giây) trước khi thực hiện tìm kiếm nếu chọn chế độ [SearchTrigger.DEBOUNCE].
 * @param searchTrigger Chế độ kích hoạt tìm kiếm: [SearchTrigger.DEBOUNCE], [SearchTrigger.IMMEDIATE], hoặc [SearchTrigger.CONFIRM].
 * @param enabled Bật/Tắt tính năng tương tác với ô tìm kiếm.
 * @param shape Hình dạng bo góc của trường nhập liệu (mặc định bo góc tròn dẹt pill shape [AppDimen.p100]).
 * @param height Chiều cao của ô tìm kiếm (mặc định là 44.dp giúp giao diện cân đối và gọn gàng).
 * @param containerColor Màu nền của ô tìm kiếm.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    value: String,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    onValueChange: ((String) -> Unit)? = null,
    placeholder: String = stringResource(Res.string.search_placeholder),
    debounceMs: Long = 500L,
    searchTrigger: SearchTrigger = SearchTrigger.DEBOUNCE,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(AppDimen.p100),
    height: Dp = 44.dp,
    containerColor: Color = Color.Transparent
) {
    var localText by remember(value) { mutableStateOf(value) }
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    // Xử lý debounce tự động
    if (searchTrigger == SearchTrigger.DEBOUNCE) {
        LaunchedEffect(localText) {
            if (localText != value) {
                delay(debounceMs.milliseconds)
                onSearch(localText)
            }
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
        focusedContainerColor = containerColor,
        unfocusedContainerColor = containerColor,
        disabledContainerColor = containerColor
    )

    BasicTextField(
        value = localText,
        onValueChange = { newVal ->
            localText = newVal
            onValueChange?.invoke(newVal)
            if (searchTrigger == SearchTrigger.IMMEDIATE) {
                onSearch(newVal)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        enabled = enabled,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(localText)
                focusManager.clearFocus()
            }
        ),
        interactionSource = interactionSource,
        decorationBox = @Composable { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = localText,
                innerTextField = innerTextField,
                enabled = enabled,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                placeholder = {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    AppIcon(
                        drawableRes = Res.drawable.ic_search_24dp,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        iconModifier = Modifier.size(AppDimen.p20)
                    )
                },
                trailingIcon = {
                    if (localText.isNotEmpty()) {
                        AppIcon(
                            drawableRes = Res.drawable.ic_close_24dp,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            iconModifier = Modifier.size(AppDimen.p20),
                            onClick = {
                                localText = ""
                                onValueChange?.invoke("")
                                onSearch("")
                                focusManager.clearFocus()
                            }
                        )
                    }
                },
                colors = textFieldColors,
                contentPadding = PaddingValues(horizontal = AppDimen.p12, vertical = 0.dp),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = enabled,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = textFieldColors,
                        shape = shape
                    )
                }
            )
        }
    )
}

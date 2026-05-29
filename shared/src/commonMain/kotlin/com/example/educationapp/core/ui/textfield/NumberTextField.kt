package com.example.educationapp.core.ui.textfield

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.educationapp.core.theme.AppDimen

/**
 * TextField chuyên dụng cho nhập Số.
 *
 * Tự động cấu hình:
 * - Bàn phím dạng Number ([KeyboardType.Number]) hoặc Decimal ([KeyboardType.Decimal]).
 * - Lọc đầu vào chỉ cho phép ký tự số (và dấu thập phân nếu cho phép).
 * - Giới hạn một dòng.
 *
 * @param value Giá trị số hiện tại (dạng String).
 * @param onValueChange Callback khi giá trị thay đổi (đã được lọc chỉ còn số).
 * @param modifier Modifier tùy chỉnh.
 * @param label Nhãn của trường.
 * @param labelStyle Kiểu hiển thị label.
 * @param placeholder Text gợi ý.
 * @param leadingIcon Icon phía trước (optional).
 * @param trailingIcon Icon phía sau (optional).
 * @param prefix Composable trước nội dung nhập (optional, ví dụ: ký hiệu tiền tệ).
 * @param suffix Composable sau nội dung nhập (optional, ví dụ: đơn vị đo).
 * @param errorMessage Thông báo lỗi validation.
 * @param enabled Cho phép tương tác.
 * @param readOnly Chỉ đọc.
 * @param allowDecimal Cho phép nhập số thập phân (dấu chấm). Mặc định là false (chỉ số nguyên).
 * @param maxLength Giới hạn số ký tự tối đa.
 * @param imeAction Hành động IME (mặc định là [ImeAction.Next]).
 * @param keyboardActions Hành động khi nhấn nút trên bàn phím.
 * @param shape Hình dạng viền.
 * @param colors Bảng màu tùy chỉnh.
 */
@Composable
fun NumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    labelStyle: AppTextFieldLabelStyle = AppTextFieldLabelStyle.Floating,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    allowDecimal: Boolean = false,
    maxLength: Int? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = RoundedCornerShape(AppDimen.p12),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        errorBorderColor = MaterialTheme.colorScheme.error,
        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
    )
) {
    val filteredOnValueChange: (String) -> Unit = { newValue ->
        val filtered = if (allowDecimal) {
            // Cho phép số và tối đa một dấu chấm thập phân
            newValue.filter { it.isDigit() || it == '.' }
                .let { text ->
                    val dotIndex = text.indexOf('.')
                    if (dotIndex != -1) {
                        // Chỉ giữ lại dấu chấm đầu tiên
                        text.substring(0, dotIndex + 1) +
                                text.substring(dotIndex + 1).replace(".", "")
                    } else {
                        text
                    }
                }
        } else {
            // Chỉ cho phép số nguyên
            newValue.filter { it.isDigit() }
        }
        onValueChange(filtered)
    }

    AppTextField(
        value = value,
        onValueChange = filteredOnValueChange,
        modifier = modifier,
        label = label,
        labelStyle = labelStyle,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        errorMessage = errorMessage,
        supportingText = supportingText,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = true,
        maxLength = maxLength,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (allowDecimal) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        shape = shape,
        colors = colors
    )
}

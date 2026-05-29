package com.example.educationapp.core.ui.textfield

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
 * TextField chuyên dụng cho nhập Email.
 *
 * Tự động cấu hình:
 * - Bàn phím dạng Email ([KeyboardType.Email]).
 * - Giới hạn một dòng.
 *
 * @param value Giá trị email hiện tại.
 * @param onValueChange Callback khi giá trị thay đổi.
 * @param modifier Modifier tùy chỉnh.
 * @param label Nhãn của trường.
 * @param labelStyle Kiểu hiển thị label.
 * @param placeholder Text gợi ý.
 * @param leadingIcon Icon phía trước (optional). Mặc định có thể truyền icon Email.
 * @param trailingIcon Icon phía sau (optional).
 * @param errorMessage Thông báo lỗi validation.
 * @param enabled Cho phép tương tác.
 * @param readOnly Chỉ đọc.
 * @param imeAction Hành động IME (mặc định là [ImeAction.Next]).
 * @param keyboardActions Hành động khi nhấn nút trên bàn phím.
 * @param shape Hình dạng viền.
 * @param colors Bảng màu tùy chỉnh.
 */
@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = "Email",
    labelStyle: AppTextFieldLabelStyle = AppTextFieldLabelStyle.Floating,
    placeholder: String? = "example@email.com",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
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
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        labelStyle = labelStyle,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        errorMessage = errorMessage,
        supportingText = supportingText,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        shape = shape,
        colors = colors
    )
}

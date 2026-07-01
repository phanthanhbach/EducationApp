package com.example.educationapp.presentation.screen.login

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.educationapp.core.theme.AppDimen
import com.example.educationapp.core.ui.textfield.AppTextField
import com.example.educationapp.core.ui.textfield.AppTextFieldLabelStyle

/**
 * TextField hỗ trợ nhập Email hoặc Số điện thoại.
 *
 * Tự động nhận diện loại đầu vào dựa trên ký tự đầu tiên:
 * - Bắt đầu bằng chữ số hoặc '+' → bàn phím dạng Phone ([KeyboardType.Phone]).
 * - Mặc định → bàn phím dạng Email ([KeyboardType.Email]).
 *
 * @param value Giá trị hiện tại.
 * @param onValueChange Callback khi giá trị thay đổi.
 * @param modifier Modifier tùy chỉnh.
 * @param label Nhãn của trường.
 * @param labelStyle Kiểu hiển thị label.
 * @param placeholder Text gợi ý.
 * @param leadingIcon Icon phía trước (optional).
 * @param trailingIcon Icon phía sau (optional).
 * @param errorMessage Thông báo lỗi validation.
 * @param supportingText Text hỗ trợ hiển thị bên dưới.
 * @param enabled Cho phép tương tác.
 * @param readOnly Chỉ đọc.
 * @param imeAction Hành động IME (mặc định là [ImeAction.Next]).
 * @param keyboardActions Hành động khi nhấn nút trên bàn phím.
 * @param shape Hình dạng viền.
 * @param colors Bảng màu tùy chỉnh.
 */
@Composable
fun EmailOrPhoneTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = "Email or Phone",
    labelStyle: AppTextFieldLabelStyle = AppTextFieldLabelStyle.Floating,
    placeholder: String? = "email@example.com or 0912345678",
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
    val isPhoneInput by remember(value) {
        derivedStateOf {
            value.firstOrNull()?.let { it.isDigit() || it == '+' } ?: false
        }
    }

    val keyboardType = if (isPhoneInput) KeyboardType.Phone else KeyboardType.Email

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
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        shape = shape,
        colors = colors
    )
}

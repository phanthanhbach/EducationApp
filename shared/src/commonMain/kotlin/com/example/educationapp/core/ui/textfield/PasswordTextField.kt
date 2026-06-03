package com.example.educationapp.core.ui.textfield

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import educationapp.shared.generated.resources.*
import com.example.educationapp.core.ui.icon.AppIcon
import com.example.educationapp.core.theme.AppDimen

/**
 * TextField chuyên dụng cho nhập Mật khẩu.
 *
 * Tự động cấu hình:
 * - Bàn phím dạng Password ([KeyboardType.Password]).
 * - Ẩn/hiện mật khẩu với nút toggle tích hợp sẵn.
 * - Giới hạn một dòng.
 *
 * @param value Giá trị mật khẩu hiện tại.
 * @param onValueChange Callback khi giá trị thay đổi.
 * @param modifier Modifier tùy chỉnh.
 * @param label Nhãn của trường.
 * @param labelStyle Kiểu hiển thị label.
 * @param placeholder Text gợi ý.
 * @param leadingIcon Icon phía trước (optional).
 * @param toggleContent Composable tùy chỉnh cho nút toggle hiển thị/ẩn mật khẩu.
 *   Nếu null sẽ dùng mặc định (icon mắt). Truyền vào để dùng icon từ Compose Resources.
 * @param errorMessage Thông báo lỗi validation.
 * @param enabled Cho phép tương tác.
 * @param readOnly Chỉ đọc.
 * @param imeAction Hành động IME (mặc định là [ImeAction.Done]).
 * @param keyboardActions Hành động khi nhấn nút trên bàn phím.
 * @param shape Hình dạng viền.
 * @param colors Bảng màu tùy chỉnh.
 */
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = "Password",
    labelStyle: AppTextFieldLabelStyle = AppTextFieldLabelStyle.Floating,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    toggleContent: @Composable ((isVisible: Boolean) -> Unit)? = null,
    errorMessage: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = RoundedCornerShape(AppDimen.p12),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        errorBorderColor = MaterialTheme.colorScheme.error,
        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
    )
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    AppTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        labelStyle = labelStyle,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                if (toggleContent != null) {
                    toggleContent(passwordVisible)
                } else {
                    AppIcon(
                        drawableRes = if (passwordVisible) Res.drawable.ic_visibility else Res.drawable.ic_visibility_off,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        errorMessage = errorMessage,
        supportingText = supportingText,
        enabled = enabled,
        readOnly = readOnly,
        singleLine = true,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        shape = shape,
        colors = colors
    )
}

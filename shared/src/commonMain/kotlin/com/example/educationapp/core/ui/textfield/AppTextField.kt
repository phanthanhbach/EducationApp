package com.example.educationapp.core.ui.textfield

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.example.educationapp.core.theme.AppDimen

/**
 * Enum cho kiểu hiển thị label của TextField.
 * - [Floating]: Label nổi (Material 3 OutlinedTextField mặc định) - label sẽ nổi lên
 *   phía trên viền khi trường được focus hoặc có nội dung.
 * - [External]: Label rời phía trên TextField, không phải là một phần của viền.
 */
enum class AppTextFieldLabelStyle {
    Floating,
    External
}

/**
 * Component TextField cơ bản (base) của toàn bộ ứng dụng.
 *
 * Mọi biến thể TextField khác (Email, Password, Number...) nên được wrap từ component này
 * để đảm bảo tính nhất quán về giao diện và hành vi trên toàn ứng dụng.
 *
 * @param value Giá trị hiện tại của trường nhập liệu.
 * @param onValueChange Callback khi giá trị thay đổi.
 * @param modifier Modifier tùy chỉnh bên ngoài.
 * @param label Nhãn của trường nhập liệu (hiển thị dạng floating hoặc external).
 * @param labelStyle Kiểu hiển thị label: [AppTextFieldLabelStyle.Floating] hoặc [AppTextFieldLabelStyle.External].
 * @param placeholder Text gợi ý hiển thị khi trường rỗng.
 * @param leadingIcon Composable hiển thị ở đầu trường (optional).
 * @param trailingIcon Composable hiển thị ở cuối trường (optional).
 * @param prefix Composable hiển thị trước nội dung nhập (optional).
 * @param suffix Composable hiển thị sau nội dung nhập (optional).
 * @param supportingText Text hỗ trợ hiển thị bên dưới trường (optional).
 * @param errorMessage Thông báo lỗi. Khi không null, trường sẽ chuyển sang trạng thái lỗi với animation.
 * @param enabled Cho phép tương tác với trường hay không.
 * @param readOnly Chỉ đọc (vẫn có thể focus nhưng không chỉnh sửa được).
 * @param singleLine Giới hạn trường chỉ trên một dòng.
 * @param maxLines Số dòng tối đa.
 * @param minLines Số dòng tối thiểu.
 * @param maxLength Giới hạn số ký tự tối đa. Khi khác null sẽ hiển thị character counter.
 * @param textStyle Kiểu chữ cho nội dung nhập liệu.
 * @param keyboardOptions Tùy chỉnh bàn phím (loại bàn phím, hành động IME...).
 * @param keyboardActions Hành động khi nhấn nút trên bàn phím (Search, Done...).
 * @param visualTransformation Biến đổi hiển thị (ví dụ: ẩn mật khẩu bằng dấu chấm).
 * @param shape Hình dạng viền của trường.
 * @param colors Bảng màu tùy chỉnh cho trường.
 * @param interactionSource Nguồn tương tác để theo dõi trạng thái focus, hover...
 */
@Composable
fun AppTextField(
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
    supportingText: String? = null,
    errorMessage: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    maxLength: Int? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    shape: Shape = RoundedCornerShape(AppDimen.p12),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        errorBorderColor = MaterialTheme.colorScheme.error,
        disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f),
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isError = errorMessage != null
    val effectiveOnValueChange: (String) -> Unit = if (maxLength != null) {
        { newValue -> if (newValue.length <= maxLength) onValueChange(newValue) }
    } else {
        onValueChange
    }

    Column(modifier = modifier) {
        // External Label
        if (label != null && labelStyle == AppTextFieldLabelStyle.External) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.padding(bottom = AppDimen.p6)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = effectiveOnValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = if (label != null && labelStyle == AppTextFieldLabelStyle.Floating) {
                { Text(text = label, style = MaterialTheme.typography.bodyMedium) }
            } else {
                null
            },
            placeholder = if (placeholder != null) {
                {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            } else {
                null
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            prefix = prefix,
            suffix = suffix,
            supportingText = if (errorMessage != null || supportingText != null || maxLength != null) {
                {
                    SupportingContent(
                        errorMessage = errorMessage,
                        supportingText = supportingText,
                        currentLength = value.length,
                        maxLength = maxLength
                    )
                }
            } else {
                null
            },
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            shape = shape,
            colors = colors,
            interactionSource = interactionSource
        )
    }
}

/**
 * Composable nội bộ hiển thị nội dung hỗ trợ bên dưới TextField,
 * bao gồm thông báo lỗi/hỗ trợ và bộ đếm ký tự.
 */
@Composable
private fun SupportingContent(
    errorMessage: String?,
    supportingText: String?,
    currentLength: Int,
    maxLength: Int?
) {
    val hasContent = errorMessage != null || supportingText != null || maxLength != null

    AnimatedVisibility(
        visible = hasContent,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Error hoặc Supporting text bên trái
            val displayText = errorMessage ?: supportingText
            if (displayText != null) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (errorMessage != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.weight(1f)
                )
            } else {
                // Spacer để đẩy counter sang bên phải
                Spacer(modifier = Modifier.weight(1f))
            }

            // Character counter bên phải
            if (maxLength != null) {
                Text(
                    text = "$currentLength/$maxLength",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (currentLength > maxLength) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(start = AppDimen.p8)
                )
            }
        }
    }
}

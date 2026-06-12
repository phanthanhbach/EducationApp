package com.example.educationapp.core.file

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual class UploadFilePicker internal constructor(
    private val launchPicker: () -> Unit
) {
    actual fun launch() = launchPicker()
}

@Composable
actual fun rememberUploadFilePicker(
    onFileSelected: (UploadFile) -> Unit,
    onError: (String) -> Unit
): UploadFilePicker {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        runCatching {
            val resolver = context.contentResolver
            val name = resolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) cursor.getString(nameIndex) else null
            } ?: uri.lastPathSegment ?: "upload-file"

            val size = resolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst() && sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                    cursor.getLong(sizeIndex)
                } else {
                    null
                }
            }

            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("Không thể đọc file đã chọn.")

            UploadFile(
                name = name,
                bytes = bytes,
                mimeType = resolver.getType(uri),
                sizeBytes = size ?: bytes.size.toLong()
            )
        }.onSuccess(onFileSelected)
            .onFailure { onError(it.message ?: "Không thể chọn file.") }
    }

    return remember(launcher) {
        UploadFilePicker {
            launcher.launch(
                arrayOf(
                    "application/pdf",
                    "application/msword",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    "application/zip",
                    "application/x-zip-compressed",
                    "image/*"
                )
            )
        }
    }
}


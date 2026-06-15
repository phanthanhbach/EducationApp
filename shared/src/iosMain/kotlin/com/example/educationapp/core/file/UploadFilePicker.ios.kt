package com.example.educationapp.core.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTTypeItem
import platform.darwin.NSObject

actual class UploadFilePicker internal constructor(
    private val onFileSelected: (UploadFile) -> Unit,
    private val onError: (String) -> Unit
) {
    private var delegate: DocumentPickerDelegate? = null

    actual fun launch() {
        val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (controller == null) {
            onError("Không thể mở trình chọn file.")
            return
        }

        val picker = UIDocumentPickerViewController(forOpeningContentTypes = listOf(UTTypeItem))
        delegate = DocumentPickerDelegate(onFileSelected, onError)
        picker.delegate = delegate
        controller.presentViewController(picker, animated = true, completion = null)
    }
}

@Composable
actual fun rememberUploadFilePicker(
    onFileSelected: (UploadFile) -> Unit,
    onError: (String) -> Unit
): UploadFilePicker = remember(onFileSelected, onError) {
    UploadFilePicker(onFileSelected, onError)
}

private class DocumentPickerDelegate(
    private val onFileSelected: (UploadFile) -> Unit,
    private val onError: (String) -> Unit
) : NSObject(), UIDocumentPickerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
        val didAccess = url.startAccessingSecurityScopedResource()

        runCatching {
            val path = url.path ?: error("Không thể đọc đường dẫn file đã chọn.")
            val data = NSFileManager.defaultManager.contentsAtPath(path)
                ?: error("Không thể đọc file đã chọn.")
            val bytes = data.bytes?.readBytes(data.length.toInt()) ?: error("Không thể đọc file đã chọn.")
            UploadFile(
                name = url.lastPathComponent ?: "upload-file",
                bytes = bytes,
                mimeType = null,
                sizeBytes = data.length.toLong()
            )
        }.onSuccess(onFileSelected)
            .onFailure { onError(it.message ?: "Không thể chọn file.") }

        if (didAccess) {
            url.stopAccessingSecurityScopedResource()
        }
    }
}

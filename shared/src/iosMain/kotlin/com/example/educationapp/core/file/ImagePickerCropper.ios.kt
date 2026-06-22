package com.example.educationapp.core.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.darwin.NSObject

actual class ImagePickerCropper internal constructor(
    private val onImageCropped: (ByteArray) -> Unit,
    private val onError: (String) -> Unit
) {
    private var delegate: ImagePickerDelegate? = null

    actual fun launch() {
        val controller = UIApplication.sharedApplication.keyWindow?.rootViewController
        if (controller == null) {
            onError("Không thể mở trình chọn ảnh.")
            return
        }

        val picker = UIImagePickerController()
        picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        picker.allowsEditing = true // Enables built-in square crop
        delegate = ImagePickerDelegate(onImageCropped, onError)
        picker.delegate = delegate
        controller.presentViewController(picker, animated = true, completion = null)
    }
}

@Composable
actual fun rememberImagePickerCropper(
    onImageCropped: (ByteArray) -> Unit,
    onError: (String) -> Unit
): ImagePickerCropper = remember(onImageCropped, onError) {
    ImagePickerCropper(onImageCropped, onError)
}

private class ImagePickerDelegate(
    private val onImageCropped: (ByteArray) -> Unit,
    private val onError: (String) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    @OptIn(ExperimentalForeignApi::class)
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        picker.dismissViewControllerAnimated(true, completion = null)

        runCatching {
            // Prefer edited (cropped) image, fallback to original
            val image = (didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage]
                ?: didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage]) as? UIImage
                ?: error("Không thể lấy ảnh đã chọn.")

            // Compress to JPEG
            val jpegData: NSData = UIImageJPEGRepresentation(image, 0.85)
                ?: error("Không thể nén ảnh.")

            val bytes = jpegData.bytes?.readBytes(jpegData.length.toInt())
                ?: error("Không thể đọc dữ liệu ảnh.")

            bytes
        }.onSuccess(onImageCropped)
            .onFailure { onError(it.message ?: "Không thể xử lý ảnh.") }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

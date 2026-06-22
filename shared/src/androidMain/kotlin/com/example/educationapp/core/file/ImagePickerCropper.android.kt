package com.example.educationapp.core.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.yalantis.ucrop.UCrop

actual class ImagePickerCropper internal constructor(
    private val launchPicker: () -> Unit
) {
    actual fun launch() = launchPicker()
}

/**
 * Custom ActivityResultContract for uCrop.
 * Input: Pair of Source Uri and Destination Uri
 * Output: Uri? of cropped image if successful, null otherwise.
 */
class UCropContract : ActivityResultContract<Pair<Uri, Uri>, Uri?>() {
    override fun createIntent(context: Context, input: Pair<Uri, Uri>): Intent {
        val (sourceUri, destinationUri) = input
        return UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .getIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == android.app.Activity.RESULT_OK && intent != null) {
            UCrop.getOutput(intent)
        } else {
            null
        }
    }
}

@Composable
actual fun rememberImagePickerCropper(
    onImageCropped: (ByteArray) -> Unit,
    onError: (String) -> Unit
): ImagePickerCropper {
    val context = LocalContext.current

    val uCropLauncher = rememberLauncherForActivityResult(
        contract = UCropContract()
    ) { croppedUri ->
        if (croppedUri != null) {
            runCatching {
                val resolver = context.contentResolver
                val inputStream = resolver.openInputStream(croppedUri)
                    ?: error("Không thể đọc ảnh sau khi crop.")
                val bytes = inputStream.use { it.readBytes() }

                // Clean up cropped temp file to free storage
                runCatching {
                    val file = java.io.File(croppedUri.path ?: "")
                    if (file.exists()) {
                        file.delete()
                    }
                }

                bytes
            }.onSuccess(onImageCropped)
                .onFailure { onError(it.message ?: "Không thể đọc ảnh đã crop.") }
        }
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        runCatching {
            val cacheDir = context.cacheDir
            val destinationFile = java.io.File(cacheDir, "cropped_avatar_${System.currentTimeMillis()}.jpg")
            val destinationUri = Uri.fromFile(destinationFile)
            uri to destinationUri
        }.onSuccess { (sourceUri, destinationUri) ->
            uCropLauncher.launch(sourceUri to destinationUri)
        }.onFailure {
            onError(it.message ?: "Không thể tạo file lưu ảnh crop.")
        }
    }

    return remember(pickerLauncher) {
        ImagePickerCropper {
            pickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }
}

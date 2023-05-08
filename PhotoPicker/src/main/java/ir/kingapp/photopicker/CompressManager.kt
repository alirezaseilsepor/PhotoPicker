package ir.kingapp.photopicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import java.io.File
import java.util.Locale

object CompressManager {

    suspend fun compress(
        context: Context,
        photoItem: PhotoItem,
        compressQuality: Int,
        compressFormat: Bitmap.CompressFormat,

        ): PhotoItem {
        val originalFile = File(photoItem.path)
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
        Log.e("compress original", (originalFile.length() / 1024).toString())
        val compressedFile = Compressor.compress(context, originalFile) {
            default(bitmap.width, bitmap.height, compressFormat, compressQuality)
            destination(getDestinationFile(context, photoItem, compressFormat))
        }
        Log.e("compress", (compressedFile.length() / 1024).toString())
        val uri = Utils.getUriFromFile(compressedFile, context)
        val length = compressedFile.length().toString()
        val path = compressedFile.absolutePath

        return PhotoItem(
            photoItem.id,
            photoItem.name,
            length,
            photoItem.originalDate,
            path,
            uri,
            photoItem.folderName
        )
    }

    private fun getDestinationFile(
        context: Context,
        photoItem: PhotoItem,
        compressFormat: Bitmap.CompressFormat,
    ): File {
        val folder = "${context.cacheDir.path}/CompressImage"
        val name = "${photoItem.id}_${System.currentTimeMillis()}"
        val format = compressFormat.name.lowercase(Locale.ENGLISH)
        val fullPath = "$folder/$name.$format"
        val file = File(fullPath)
        file.mkdirs()
        return file
    }

}
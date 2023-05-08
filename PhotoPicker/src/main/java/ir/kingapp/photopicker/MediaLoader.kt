package ir.kingapp.photopicker

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.core.os.bundleOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MediaLoader {

    private const val IMAGE_PAGE_SIZE = 15 * 3
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    suspend fun getPhotoItems(
        context: Context,
        currentPage: Int,
        pageSize: Int = IMAGE_PAGE_SIZE,
    ): ArrayList<PhotoItem> {
        return withContext(Dispatchers.IO) {
            val imageItems = ArrayList<PhotoItem>()
            val offset = pageSize * currentPage
            val cursor = createCursor(context, pageSize, offset)

            cursor?.use {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                val nameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val dateTakenColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val dateAddColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val folderColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                while (it.moveToNext()) {
                    val id = it.getLong(idColumn)
                    val name = it.getStringOrNull(nameColumn)
                    val size = it.getStringOrNull(sizeColumn)
                    val dateTaken = it.getLongOrNull(dateTakenColumn)
                    val dateAdded = it.getLong(dateAddColumn)
                    val path = it.getString(pathColumn)
                    val folder = it.getStringOrNull(folderColumn) ?: context.getString(R.string.without_name)
                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    val originalDate = dateTaken ?: dateAdded

                    if (name != null) {
                        val resultMedia = PhotoItem(
                            id,
                            name,
                            size,
                            originalDate,
                            path,
                            uri,
                            folder,
                        )
                        imageItems.add(resultMedia)
                    }

                }
            }
            imageItems
        }
    }


    private fun createCursor(context: Context, limit: Int, offset: Int): Cursor? {
        val contentResolver = context.contentResolver
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val bundle = bundleOf(
                ContentResolver.QUERY_ARG_OFFSET to offset,
                ContentResolver.QUERY_ARG_LIMIT to limit,
                ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Images.Media.DATE_ADDED),
                ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                bundle,
                null
            )
        } else {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit OFFSET $offset",
                null
            )
        }
    }
}
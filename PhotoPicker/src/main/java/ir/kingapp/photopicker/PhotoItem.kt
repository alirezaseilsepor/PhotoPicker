package ir.kingapp.photopicker

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoItem(
    val id: Long,
    val name: String,
    val size: String?,
    val originalDate: Long,
    var path: String,
    val uri: Uri,
    val folderName: String,
) : Parcelable
package ir.kingapp.photopicker

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import app.king.mylibrary.ktx.Click
import app.king.mylibrary.ktx.safeDismiss
import app.king.mylibrary.ktx.setOnSafeClickListener
import com.canhub.cropper.CropImageOptions
import ir.kingapp.photopicker.databinding.DialogCropBinding
import java.io.File

class CropDialog(
    private val photoItem: PhotoItem,
    private val cropImageOptions: CropImageOptions,
) : DialogFragment() {

    private var binding: DialogCropBinding? = null
    var onCropListener: Click<PhotoItem>? = null

    private fun setup() {
        binding!!.apply {
            cropImageView.setImageCropOptions(cropImageOptions)
            cropImageView.setImageUriAsync(photoItem.uri)
            btnCrop.setOnSafeClickListener {
                btnCrop.isEnabled = false
                cropImageView.croppedImageAsync(saveCompressQuality = 100)
            }
            cropImageView.setOnCropImageCompleteListener { _, result ->
                if (result.isSuccessful) {
                    val uri = result.originalUri!!
                    val path = result.getUriFilePath(requireContext(), true)!!
                    val length = File(path).length().toString()
                    val item = PhotoItem(
                        photoItem.id,
                        photoItem.name,
                        length,
                        photoItem.originalDate,
                        path,
                        uri,
                        photoItem.folderName,
                    )
                    onCropListener?.invoke(item)
                    safeDismiss()
                }
                btnCrop.isEnabled = true
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(
            requireContext(),
            R.style.FullScreenCropDialog
        ) {}
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogCropBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
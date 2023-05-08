package ir.kingapp.picker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import ir.kingapp.photopicker.PhotoPickerDialog
import ir.kingapp.picker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpen.setOnClickListener {
            val photoPickerDialog = PhotoPickerDialog.Builder()
                .maxSelectSize(3)
                .crop(CropImageOptions().apply {
                    cropShape = CropImageView.CropShape.OVAL
                    cornerShape = CropImageView.CropCornerShape.OVAL
                    aspectRatioX = 1
                    aspectRatioY = 1
                    minCropResultWidth = 200
                    minCropResultHeight = 200
                    fixAspectRatio = true
                    guidelines = CropImageView.Guidelines.ON_TOUCH
                })
                .compress(80)
                .onSelectListener {
                    binding.image1.load(it[0].uri)
                    binding.image2.load(it[1].uri)
                    binding.image3.load(it[2].uri)
                }
                .build()
            photoPickerDialog.show(supportFragmentManager, null)
        }

    }
}
package ir.kingapp.picker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ir.kingapp.photopicker.PhotoPickerDialog
import ir.kingapp.picker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val a: PhotoPickerDialog = PhotoPickerDialog.Builder()
            .maxSelectSize(5)
            .crop()
            .compress()
            .build()
        a.show(supportFragmentManager, null)
    }
}
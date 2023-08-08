package ir.kingapp.photopicker

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable


abstract class BaseDialogPicker<VB : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = _binding as VB


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(true)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val newMaterialShapeDrawable = createMaterialShapeDrawable(bottomSheet)
            ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable)
        }
        return dialog
    }

    private fun createMaterialShapeDrawable(bottomSheet: View): Drawable? {
        val currentMaterialShapeDrawable = bottomSheet.background as? MaterialShapeDrawable
        val currentMaterialShapeDrawable2 = bottomSheet.background as? GradientDrawable
        currentMaterialShapeDrawable?.fillColor = ColorStateList.valueOf(getBgColor())
        currentMaterialShapeDrawable2?.color = ColorStateList.valueOf(getBgColor())
        return currentMaterialShapeDrawable ?: currentMaterialShapeDrawable2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }


    abstract fun setup()

    abstract fun getBgColor(): Int

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
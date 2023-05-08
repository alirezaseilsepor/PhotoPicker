package ir.kingapp.photopicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.skydoves.powermenu.MenuBaseAdapter


class FolderMenuAdapter : MenuBaseAdapter<String>() {
    override fun getView(index: Int, view: View?, viewGroup: ViewGroup?): View {
        val context = viewGroup?.context
        var currentView = view
        if (view == null) {
            val inflater =
                context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            currentView = inflater.inflate(R.layout.item_menu_folder, viewGroup, false)
        }
        val item = getItem(index) as String
        val titleView = currentView?.findViewById<TextView>(R.id.tv_name)
        titleView?.text = item
        return super.getView(index, currentView, viewGroup)
    }


}
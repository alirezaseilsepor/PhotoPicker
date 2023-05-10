package ir.kingapp.photopicker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.king.mylibrary.ktx.setOnSafeClickListener
import coil.load
import ir.kingapp.photopicker.databinding.ItemMediaBinding

class PhotoPickerAdapter(private val maxSelectSize: Int) :
    ListAdapter<PhotoItem, RecyclerView.ViewHolder>(DIFF_PICK_MEDIA) {

    private val selectedItems = ArrayList<PhotoItem>()
    private val selectedItemsPosition = ArrayList<Int>()
    private var lastSelectPhotoItem: PhotoItem? = null
    var selectedItemsListener: ((ArrayList<PhotoItem>) -> Unit)? = null

    override fun submitList(list: List<PhotoItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderPickMedia(
            ItemMediaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderPickMedia).bind(getItem(position))
    }


    private inner class ViewHolderPickMedia(private val binding: ItemMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(photoItem: PhotoItem) {
            binding.imageView.load(photoItem.uri) {
                crossfade(true)
            }
            selectOrUnselect(photoItem)
            binding.root.setOnSafeClickListener {
                lastSelectPhotoItem = photoItem
                if (isSingleSelect())
                    setClickSingleSelectListener(photoItem)
                else
                    setClickMultiSelectListener(photoItem)

                updateItems()
                selectedItemsListener?.invoke(selectedItems)
            }
        }

        private fun setClickMultiSelectListener(photoItem: PhotoItem) {
            if (isSelected(photoItem)) {
                selectedItems.remove(photoItem)
                selectedItemsPosition.remove(adapterPosition)
            } else {
                if (isMaxSelectedSize())
                    return
                selectedItems.remove(photoItem)
                selectedItems.add(photoItem)
                selectedItemsPosition.add(adapterPosition)
            }
        }

        private fun setClickSingleSelectListener(photoItem: PhotoItem) {
            if (isSelected(photoItem)) {
                selectedItems.remove(photoItem)
                selectedItemsPosition.remove(adapterPosition)
            } else {
                selectedItems.clear()
                selectedItems.add(photoItem)
                selectedItemsPosition.add(adapterPosition)
            }
        }

        private fun selectOrUnselect(photoItem: PhotoItem) {
            if (isSelected(photoItem))
                select(photoItem)
            else
                unselect(photoItem)
        }

        @SuppressLint("SetTextI18n")
        private fun select(photoItem: PhotoItem) {
            animScale(binding.imageView, true, photoItem == lastSelectPhotoItem)
            binding.tvSelect.setBackgroundResource(getBackgroundSelectedCircle())
            if (!isSingleSelect())
                binding.tvSelect.text = (selectedItems.indexOf(photoItem) + 1).toString()
        }

        private fun unselect(photoItem: PhotoItem) {
            animScale(binding.imageView, false, photoItem == lastSelectPhotoItem)
            binding.tvSelect.setBackgroundResource(R.drawable.shape_circle_unselected)
            binding.tvSelect.text = ""
        }

        private fun getBackgroundSelectedCircle(): Int {
            return if (isSingleSelect())
                R.drawable.ic_check_file_picker
            else
                R.drawable.shape_circle_selected
        }

        private fun isSelected(photoItem: PhotoItem): Boolean {
            return selectedItems.any { it.id == photoItem.id }
        }

        private fun isMaxSelectedSize(): Boolean {
            return selectedItems.size >= maxSelectSize
        }

        private fun isSingleSelect(): Boolean {
            return maxSelectSize <= 1
        }

        private fun animScale(view: View, isSelected: Boolean, isAnimation: Boolean) {
            var duration = 200
            if (!isAnimation) duration = 0
            var toScale = if (isSelected) 0.75f else 1.0f
            if (isSelected && isSingleSelect())
                toScale = 0.9f
            ViewCompat.animate(view)
                .setDuration(duration.toLong())
                .scaleX(toScale)
                .scaleY(toScale)
                .start()
        }

        private fun updateItems() {
            notifyItemChanged(adapterPosition)
            selectedItemsPosition.forEach {
                notifyItemChanged(it)
            }
        }
    }
}

val DIFF_PICK_MEDIA = object : DiffUtil.ItemCallback<PhotoItem>() {
    override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
        return oldItem == newItem
    }
}
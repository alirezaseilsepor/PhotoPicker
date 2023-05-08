package ir.kingapp.photopicker


import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.lifecycle.lifecycleScope
import app.king.mylibrary.ktx.Click
import app.king.mylibrary.ktx.safeDismiss
import app.king.mylibrary.ktx.setOnSafeClickListener
import app.king.mylibrary.ktx.setSafeScrollListener
import app.king.mylibrary.util.EndlessRecyclerViewScrollListener
import com.permissionx.guolindev.PermissionX
import com.skydoves.powermenu.CustomPowerMenu
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import ir.kingapp.photopicker.databinding.DialogPhotoPickerBinding
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Vector


class PhotoPickerDialog private constructor(
    private val maxSelectSize: Int,
    private val isCropEnabled: Boolean,
    private val cropWidthRatio: Int,
    private val cropHeightRatio: Int,
    private val isCompressEnabled: Boolean,
    private val compressQuality: Int,
    private val compressFormat: Bitmap.CompressFormat,
    private val onSelectListener: Click<ArrayList<PhotoItem>>?,
) : BaseDialogPicker<DialogPhotoPickerBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogPhotoPickerBinding
        get() = DialogPhotoPickerBinding::inflate

    private var isCacheCropEnabled = isCropEnabled
    private var isCacheCompressEnabled = isCompressEnabled

    private var selectedItems = ArrayList<PhotoItem>()
    private val listPhotoItem = Vector<PhotoItem>()
    private var powerMenu: CustomPowerMenu<String, FolderMenuAdapter>? = null
    private val photoPickerAdapter = PhotoPickerAdapter(maxSelectSize)
    private val endlessScrollListener = object :
        EndlessRecyclerViewScrollListener(0, 10) {
        override fun onLoadMore(page: Int) {
            getNextPagePhoto(page)
        }

        override fun isLastPage(): Boolean = false
    }


    override fun setup() {
        requestStoragePermission { isGrant ->
            if (isGrant) {
                showMedia()
            } else
                safeDismiss()
        }
    }


    private fun requestStoragePermission(action: (Boolean) -> Unit) {
        val permissionX =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                PermissionX.init(this)!!.permissions(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                PermissionX.init(this)!!.permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        permissionX
            .onExplainRequestReason { scope, deniedList ->
                /* scope.showRequestReasonDialog(
                     deniedList,
                     getString(R.string.permission_request_title),
                     getString(R.string.ok),
                     getString(R.string.cancel)
                 )*/
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    getString(R.string.permission_forward_setting),
                    getString(R.string.ok),
                    getString(R.string.cancel)
                )
            }
        permissionX
            .request { allGranted, _, _ ->
                action.invoke(allGranted)
            }
    }

    private fun showMedia() {
        binding.recyclerView.itemAnimator = null
        binding.recyclerView.adapter = photoPickerAdapter
        photoPickerAdapter.selectedItemsListener = { selectedList ->
            selectedItems = selectedList
            binding.btnConfirm.isEnabled = selectedList.isNotEmpty()
        }
        binding.recyclerView.setSafeScrollListener(endlessScrollListener)
        if (photoPickerAdapter.currentList.isEmpty())
            getNextPagePhoto(endlessScrollListener.currentPage)

        binding.tvFolder.setOnSafeClickListener {
            powerMenu?.showAsDropDown(binding.tvFolder)
        }

        binding.btnConfirm.setOnSafeClickListener {
            clickConfirm()
        }
    }

    private fun clickConfirm() {
        if (isCacheCropEnabled) {
            cropPhotoItems()
        } else if (isCacheCompressEnabled) {
            compressPhotoItems()
        } else{
            onSelectListener?.invoke(selectedItems)
            safeDismiss()
        }
    }

    private fun cropPhotoItems() {
        isCacheCropEnabled = false
        clickConfirm()
    }

    private fun compressPhotoItems() {
        lifecycleScope.launch {
            val list = ArrayList<PhotoItem>()
            selectedItems.forEach {
                val compressItem = CompressManager.compress(
                    requireContext(),
                    it,
                    compressQuality,
                    compressFormat
                )
                list.add(compressItem)
            }
            selectedItems = list
            isCacheCompressEnabled = false
            clickConfirm()
        }
    }


    private fun getNextPagePhoto(page: Int) {
        lifecycleScope.launch {
            //I had to use a Int.MAX_VALUE for the list of folders.
            if (listPhotoItem.isNotEmpty())
                return@launch
            val newList = MediaLoader.getPhotoItems(requireContext(), page, Int.MAX_VALUE)
            listPhotoItem.addAll(newList)
            photoPickerAdapter.submitList(listPhotoItem)
            setListFolder()
        }
    }

    private fun setListFolder() {
        val listFolderPhotoItem = ArrayList<String>()
        listFolderPhotoItem.add(requireContext().getString(R.string.all_folder))
        val distinctList = listPhotoItem
            .map { it.folderName }
            .distinct()
            .sortedBy { it.lowercase(Locale.ENGLISH) }
        listFolderPhotoItem.addAll(distinctList)
        val listMenu = listFolderPhotoItem.map { it }
        powerMenu = CustomPowerMenu.Builder<String, FolderMenuAdapter>(
            requireContext(),
            FolderMenuAdapter()
        )
            .addItemList(listMenu)
            .setAnimation(MenuAnimation.DROP_DOWN)
            .setAutoDismiss(true)
            .setLifecycleOwner(this)
            .setOnMenuItemClickListener(object : OnMenuItemClickListener<String> {
                override fun onItemClick(position: Int, item: String) {
                    binding.tvFolder.text = item
                    photoPickerAdapter.submitList(null)
                    if (position != 0)
                        photoPickerAdapter.submitList(listPhotoItem.filter { it.folderName == item })
                    else
                        photoPickerAdapter.submitList(listPhotoItem)
                }
            })
            .build()

    }


    class Builder {
        private var onSelectListener: Click<ArrayList<PhotoItem>>? = null
        private var maxSelectSize: Int = 1
        private var isCropEnabled: Boolean = false
        private var cropWidthRatio: Int = -1
        private var cropHeightRatio: Int = -1
        private var isCompressEnabled: Boolean = false
        private var compressQuality: Int = 100
        private var compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG

        fun maxSelectSize(size: Int): Builder = apply { this.maxSelectSize = size }

        fun crop(): Builder = apply {
            this.isCropEnabled = true
        }

        fun crop(ratio: Int): Builder = apply {
            this.isCropEnabled = true
            this.cropWidthRatio = ratio
            this.cropHeightRatio = ratio
        }

        fun crop(widthRatio: Int, heightRatio: Int): Builder = apply {
            this.isCropEnabled = true
            this.cropWidthRatio = widthRatio
            this.cropHeightRatio = heightRatio
        }

        fun compress(
            @IntRange(from = 0, to = 100) quality: Int,
            format: Bitmap.CompressFormat = compressFormat,
        ): Builder =
            apply {
                this.isCompressEnabled = true
                this.compressQuality = quality
                this.compressFormat = format
            }

        fun onSelectListener(onSelectListener: Click<ArrayList<PhotoItem>>): Builder =
            apply {
                this.onSelectListener = onSelectListener
            }


        fun build(): PhotoPickerDialog {
            return PhotoPickerDialog(
                maxSelectSize,
                isCropEnabled,
                cropWidthRatio,
                cropHeightRatio,
                isCompressEnabled,
                compressQuality,
                compressFormat,
                onSelectListener
            )
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.clearOnScrollListeners()
        super.onDestroyView()
    }
}

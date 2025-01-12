package com.dscreate_app.cashbash.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.activities.EditAdsActivity
import com.dscreate_app.cashbash.adapters.SelectImageAdapter
import com.dscreate_app.cashbash.databinding.FragmentImageListBinding
import com.dscreate_app.cashbash.utils.callbacks.AdapterCallback
import com.dscreate_app.cashbash.utils.callbacks.ItemTouchMoveCallback
import com.dscreate_app.cashbash.utils.dialogs.ProgressDialog
import com.dscreate_app.cashbash.utils.image_picker.PixImagePicker
import com.dscreate_app.cashbash.utils.image_picker.ImageConst
import com.dscreate_app.cashbash.utils.image_picker.ImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFragment(private val fragClose: FragmentClose): BaseAdsFragment(),
    AdapterCallback {

    private val adapter = SelectImageAdapter(this)
    private val dragCallback = ItemTouchMoveCallback(adapter)
    private val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null
    private var addImageItem: MenuItem? = null

    private var _binding: FragmentImageListBinding? = null
    private val binding: FragmentImageListBinding
        get() = _binding ?: throw RuntimeException("FragmentImageListBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageListBinding.inflate(inflater, container, false)
        adView = binding.adView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        init()
    }

    override fun onCloseInterAd() {
        super.onCloseInterAd()
        closeThisFragment()
        fragClose.onClose(adapter.mainList)
        job?.cancel()
    }

    private fun init() = with(binding) {
        rcViewSelectImage.layoutManager = LinearLayoutManager(requireContext())
        touchHelper.attachToRecyclerView(rcViewSelectImage)
        rcViewSelectImage.adapter = adapter
    }

    private fun setupToolbar() = with(binding) {
        toolbar.inflateMenu(R.menu.choose_image__menu)
        val deleteImageItem = toolbar.menu.findItem(R.id.delete_image)
        addImageItem = toolbar.menu.findItem(R.id.add_image)
        if (adapter.mainList.size > 2) {
            addImageItem?.isVisible = false
        }

        toolbar.setNavigationOnClickListener {
            showInterAd()
        }

        deleteImageItem.setOnMenuItemClickListener {
            adapter.updateAdapter(mutableListOf(), true)
            addImageItem?.isVisible = true
            true
        }
        addImageItem?.setOnMenuItemClickListener {
            if (adapter.mainList.size > 2) {
                addImageItem?.isVisible = false
            }
            val imageCount = ImageConst.MAX_COUNT_IMAGES - adapter.mainList.size
            PixImagePicker.addImages(activity as EditAdsActivity, imageCount)
            true
        }
    }

    private fun closeThisFragment() {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
    }

    fun updateAdapter(newList: MutableList<Uri>, activity: Activity) {
        resizeSelectedImages(newList, false, activity)
    }

    fun updateAdapterFromEdit(bitmapList: MutableList<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    fun setSingeImage(uri: Uri, position: Int) {
        val pBar = binding.rcViewSelectImage[position]
            .findViewById<ProgressBar>(R.id.pBar)

        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility = View.VISIBLE
            val bitmapList =  ImageManager.imageResize(mutableListOf(uri), activity as Activity)
            pBar.visibility = View.GONE
            adapter.mainList[position] = bitmapList[0]
            adapter.notifyItemChanged(position)
        }
    }

    fun resizeSelectedImages(newList: MutableList<Uri>, needClear: Boolean, activity: Activity) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity)
            val bitmapList = ImageManager.imageResize(newList, activity)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.mainList.size > 2) {
                addImageItem?.isVisible = false
            }
        }
    }

    interface FragmentClose {
        fun onClose(list: MutableList<Bitmap>)
    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }

    companion object {

        private const val TAG = "MyLog"

        @JvmStatic
        fun newInstance(fragClose: FragmentClose) = ImageListFragment(fragClose)
    }


}
package com.dscreate_app.cashbash.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscreate_app.cashbash.R
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

class ImageListFragment(
    private val fragClose: FragmentClose,
    private val newList: MutableList<String>?
    ): Fragment(), AdapterCallback {

    private var _binding: FragmentImageListBinding? = null
    private val binding: FragmentImageListBinding
        get() = _binding ?: throw RuntimeException("FragmentImageListBinding is null")

    private val adapter = SelectImageAdapter(this)
    private val dragCallback = ItemTouchMoveCallback(adapter)
    private val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null
    private var addImageItem: MenuItem? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        init()
        newList?.let { resizeSelectedImages(newList, true) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
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

        toolbar.setNavigationOnClickListener {
            closeThisFragment()
        }

        deleteImageItem.setOnMenuItemClickListener {
            adapter.updateAdapter(mutableListOf(), true)
            addImageItem?.isVisible = true
            true
        }
        addImageItem?.setOnMenuItemClickListener {
            val imageCount = ImageConst.MAX_COUNT_IMAGES - adapter.mainList.size
            PixImagePicker.getImages(
                activity as AppCompatActivity,
                imageCount,
                ImageConst.REQUEST_CODE_GET_IMAGES)
            true
        }
    }

    private fun closeThisFragment() {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
    }

    fun updateAdapter(newList: MutableList<String>) {
        resizeSelectedImages(newList, false)
    }

    fun updateAdapterFromEdit(bitmapList: MutableList<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    fun setSingeImage(uri: String, position: Int) {
        val pBar = binding.rcViewSelectImage[position]
            .findViewById<ProgressBar>(R.id.pBar)

        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility = View.VISIBLE
            val bitmapList =  ImageManager.imageResize(listOf(uri))
            pBar.visibility = View.GONE
            adapter.mainList[position] = bitmapList[0]
            adapter.notifyItemChanged(position)
        }

    }

    private fun resizeSelectedImages(newList: MutableList<String>, needClear: Boolean) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(requireContext())
            val bitmapList = ImageManager.imageResize(newList)
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
        fun newInstance(fragClose: FragmentClose, newList: MutableList<String>?) =
            ImageListFragment(fragClose, newList)
    }


}
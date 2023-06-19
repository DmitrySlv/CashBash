package com.dscreate_app.cashbash.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.SelectImageAdapter
import com.dscreate_app.cashbash.databinding.FragmentImageListBinding
import com.dscreate_app.cashbash.utils.callbacks.ItemTouchMoveCallback
import com.dscreate_app.cashbash.utils.image_picker.PixImagePicker
import com.dscreate_app.cashbash.utils.image_picker.ImageConst
import com.dscreate_app.cashbash.utils.image_picker.ImageManager
import com.dscreate_app.cashbash.utils.logD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageListFragment(
    private val fragClose: FragmentClose,
    private val newList: MutableList<String>?
    ): Fragment() {

    private var _binding: FragmentImageListBinding? = null
    private val binding: FragmentImageListBinding
        get() = _binding ?: throw RuntimeException("FragmentImageListBinding is null")

    private val adapter = SelectImageAdapter()
    private val dragCallback = ItemTouchMoveCallback(adapter)
    private val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null


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
        if (newList != null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                val bitmapList = ImageManager.imageResize(newList)
                adapter.updateAdapter(bitmapList, true)
            }
        }
    }

    fun updateAdapterFromEdit(bitmapList: MutableList<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
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
        val addImageItem = toolbar.menu.findItem(R.id.add_image)

        toolbar.setNavigationOnClickListener {
            closeThisFragment()
        }

        deleteImageItem.setOnMenuItemClickListener {
            adapter.updateAdapter(mutableListOf(), true)
            true
        }
        addImageItem.setOnMenuItemClickListener {
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
        job = CoroutineScope(Dispatchers.Main).launch {
            val bitmapList =  ImageManager.imageResize(newList)
            adapter.updateAdapter(bitmapList, false)
        }
    }

    fun setSingeImage(uri: String, position: Int) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val bitmapList =  ImageManager.imageResize(listOf(uri))
            adapter.mainList[position] = bitmapList[0]
            adapter.notifyDataSetChanged()
        }

    }

    interface FragmentClose {
        fun onClose(list: MutableList<Bitmap>)
    }

    companion object {

        private const val TAG = "MyLog"

        @JvmStatic
        fun newInstance(fragClose: FragmentClose, newList: MutableList<String>?) =
            ImageListFragment(fragClose, newList)
    }
}
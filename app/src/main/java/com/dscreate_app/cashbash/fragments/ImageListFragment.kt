package com.dscreate_app.cashbash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.ImageAdapter
import com.dscreate_app.cashbash.adapters.SelectImageAdapter
import com.dscreate_app.cashbash.databinding.FragmentImageListBinding
import com.dscreate_app.cashbash.utils.callbacks.ItemTouchMoveCallback
import com.dscreate_app.cashbash.utils.image_picker.ImagePicker
import com.dscreate_app.cashbash.utils.image_picker.ImagePickerConst

class ImageListFragment(
    private val fragClose: FragmentClose,
    private val newList: MutableList<String>
    ): Fragment() {

    private var _binding: FragmentImageListBinding? = null
    private val binding: FragmentImageListBinding
        get() = _binding ?: throw RuntimeException("FragmentImageListBinding is null")

    private val adapter = SelectImageAdapter()
    private val dragCallback = ItemTouchMoveCallback(adapter)
    private val touchHelper = ItemTouchHelper(dragCallback)


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
        updateList()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        fragClose.onClose(adapter.mainList)
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
            val imageCount = ImagePickerConst.MAX_COUNT_IMAGES - adapter.mainList.size
            ImagePicker.getImages(activity as AppCompatActivity, imageCount)
            true
        }
    }

    private fun closeThisFragment() {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
    }

    private fun updateList() {
        adapter.updateAdapter(newList, true)
    }

    fun updateAdapter(newList: MutableList<String>) {
        adapter.updateAdapter(newList, false)
    }

    interface FragmentClose {
        fun onClose(list: MutableList<String>)
    }

    companion object {

        private const val TAG = "MyLog"

        @JvmStatic
        fun newInstance(fragClose: FragmentClose, newList: MutableList<String>) =
            ImageListFragment(fragClose, newList)
    }
}
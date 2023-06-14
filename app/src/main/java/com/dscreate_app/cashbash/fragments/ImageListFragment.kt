package com.dscreate_app.cashbash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscreate_app.cashbash.adapters.SelectImageAdapter
import com.dscreate_app.cashbash.databinding.FragmentImageListBinding
import com.dscreate_app.cashbash.models.SelectImageItem
import com.dscreate_app.cashbash.utils.callbacks.ItemTouchMoveCallback
import com.dscreate_app.cashbash.utils.logD

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
        init()
        clickBackEdAdsActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        fragClose.onClose()
        logD(TAG, "Title 0 : ${adapter.mainList[0].title}")
        logD(TAG, "Title 1 : ${adapter.mainList[1].title}")
        logD(TAG, "Title 2 : ${adapter.mainList[2].title}")
    }

    private fun init() = with(binding) {
        rcViewSelectImage.layoutManager = LinearLayoutManager(requireContext())
        touchHelper.attachToRecyclerView(rcViewSelectImage)
        rcViewSelectImage.adapter = adapter
        updateList()
    }

    private fun clickBackEdAdsActivity() {
        binding.bBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }
    }

    private fun updateList() {
        val list = mutableListOf<SelectImageItem>()
        for (i in 0 until newList.size) {
            list.add(SelectImageItem(i.toString(), newList[i]))
        }
        adapter.updateAdapter(list)
    }

    interface FragmentClose {
        fun onClose()
    }

    companion object {

        private const val TAG = "MyLog"

        @JvmStatic
        fun newInstance(
            fragClose: FragmentClose, newList: MutableList<String>
        ) = ImageListFragment(fragClose, newList)
    }
}
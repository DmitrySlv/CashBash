package com.dscreate_app.cashbash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscreate_app.cashbash.adapters.SelectImageAdapter
import com.dscreate_app.cashbash.databinding.FragmentImageListBinding
import com.dscreate_app.cashbash.models.SelectImageItem

class ImageListFragment(
    private val fragClose: FragmentClose,
    private val newList: ArrayList<String>
    ): Fragment() {

    private var _binding: FragmentImageListBinding? = null
    private val binding: FragmentImageListBinding
        get() = _binding ?: throw RuntimeException("FragmentImageListBinding is null")
    private lateinit var adapter: SelectImageAdapter


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
        binding.bBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        fragClose.onClose()
    }

    private fun init() = with(binding) {
        rcViewSelectImage.layoutManager = LinearLayoutManager(requireContext())
        adapter = SelectImageAdapter()
        rcViewSelectImage.adapter = adapter
        val list = ArrayList<SelectImageItem>()
        for (i in 0 until newList.size) {
            list.add(SelectImageItem(i.toString(), newList[i]))
        }
        adapter.submitList(list)
    }


    interface FragmentClose {
        fun onClose()
    }

    companion object {

        @JvmStatic
        fun newInstance(
            fragClose: FragmentClose, newList: ArrayList<String>
        ) = ImageListFragment(fragClose, newList)
    }
}
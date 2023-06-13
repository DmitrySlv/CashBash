package com.dscreate_app.cashbash.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dscreate_app.cashbash.databinding.FragmentImageListBinding

class ImageListFragment(private val fragClose: FragmentClose): Fragment() {

    private var _binding: FragmentImageListBinding? = null
    private val binding: FragmentImageListBinding
        get() = _binding ?: throw RuntimeException("FragmentImageListBinding is null")

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


    interface FragmentClose {
        fun onClose()
    }

    companion object {

        @JvmStatic
        fun newInstance(fragClose: FragmentClose) = ImageListFragment(fragClose)
    }
}
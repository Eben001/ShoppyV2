package com.ebenezer.gana.shoppy.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.FragmentProductsBinding


open class BaseFragment : Fragment() {
    private var _binding:FragmentProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mProgressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root


    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(requireActivity())
        with(mProgressDialog){
            setContentView(R.layout.dialog_progress)
            findViewById<TextView>(R.id.tv_progress_text).text = text
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }


    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
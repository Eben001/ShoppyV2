package com.ebenezer.gana.shoppyv2.ui.fragments

import android.app.Dialog
import androidx.fragment.app.Fragment
import android.widget.TextView
import com.ebenezer.gana.shoppyv2.R


open class BaseFragment : Fragment() {


    private lateinit var mProgressDialog: Dialog



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


}
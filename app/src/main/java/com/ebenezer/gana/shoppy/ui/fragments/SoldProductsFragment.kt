package com.ebenezer.gana.shoppy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.FragmentSoldProductsBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.Order
import com.ebenezer.gana.shoppy.models.SoldProduct
import com.ebenezer.gana.shoppy.ui.adapters.SoldProductListAdapter

class SoldProductsFragment : BaseFragment() {

    private var _binding: FragmentSoldProductsBinding? = null

    private val binding get() = _binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSoldProductsBinding.inflate(inflater, container, false)



        return binding?.root

    }

    override fun onResume() {
        super.onResume()
        getSoldProductsList()
    }

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
        hideProgressDialog()
        if (soldProductsList.size > 0) {
            binding!!.rvSoldProductItems.visibility = View.VISIBLE
            binding!!.tvNoSoldProductsFound.visibility = View.GONE


            binding!!.rvSoldProductItems.apply {
                layoutManager = LinearLayoutManager(activity)
                setHasFixedSize(true)

                adapter = SoldProductListAdapter(requireActivity(), soldProductsList)

            }
        } else {
            binding!!.rvSoldProductItems.visibility = View.GONE
            binding!!.tvNoSoldProductsFound.visibility = View.VISIBLE


        }

    }



    private fun getSoldProductsList() {
        // show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))

        // call the function of FireStore class
        FirestoreClass().getSoldProductsList(this@SoldProductsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
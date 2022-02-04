package com.ebenezer.gana.shoppy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.FragmentSoldProductsBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.Order
import com.ebenezer.gana.shoppy.models.SoldProduct
import com.ebenezer.gana.shoppy.ui.adapters.SoldProductListAdapter
import com.ebenezer.gana.shoppy.utils.SwipeToDeleteCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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


    private fun showAlertDialogToDeleteASoldProduct(userId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.delete_dialog_title))
            .setMessage(resources.getString(R.string.delete_all_orders_dialog_message))

            .setIcon(R.drawable.ic_iv_svg_delete)
            .setNeutralButton(resources.getString(R.string.cancel_dialog_message)) { dialog, _ ->
                dialog.cancel()

            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                //show progress dialog to do the actual deletion from the cloud
                showProgressDialog(resources.getString(R.string.please_wait))

                FirestoreClass().deleteASoldProduct(this, userId)
                dialog.dismiss()
            }
            .show()
    }


    fun deleteASoldProduct(Id: String) {

        showAlertDialogToDeleteASoldProduct(Id)
    }


    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
        hideProgressDialog()
        if (soldProductsList.size > 0) {
            binding!!.rvSoldProductItems.visibility = View.VISIBLE
            binding!!.tvNoSoldProductsFound.visibility = View.GONE


            binding!!.rvSoldProductItems.apply {
                layoutManager = LinearLayoutManager(activity)
                setHasFixedSize(true)

                adapter = SoldProductListAdapter(requireActivity(), soldProductsList, this@SoldProductsFragment)



                val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteASoldProduct(
                             this@SoldProductsFragment,
                             soldProductsList[viewHolder.adapterPosition].id
                         )

                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding!!.rvSoldProductItems)

            }
        } else {
            binding!!.rvSoldProductItems.visibility = View.GONE
            binding!!.tvNoSoldProductsFound.visibility = View.VISIBLE


        }

    }


    fun successDeletingASoldProduct() {
        //hide the progress dialog, first shown in the alertDialog
        hideProgressDialog()
        Toast.makeText(
            requireActivity(), resources.getString(R.string.all_orders_delete_success_message),


            Toast.LENGTH_SHORT
        ).show()

        getSoldProductsList()

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
package com.ebenezer.gana.shoppyv2.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppyv2.R

import com.ebenezer.gana.shoppyv2.databinding.FragmentOrdersBinding
import com.ebenezer.gana.shoppyv2.firestore.FirestoreClass
import com.ebenezer.gana.shoppyv2.models.Order
import com.ebenezer.gana.shoppyv2.ui.adapters.MyOrdersListAdapter
import com.ebenezer.gana.shoppyv2.utils.SwipeToDeleteCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OrdersFragment : BaseFragment() {

    //private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var mUserId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)



        return binding.root
    }


    fun successDeleteAllOrders() {
        //hide the progress dialog, first shown in the alertDialog
        hideProgressDialog()
        Toast.makeText(
            requireActivity(), resources.getString(R.string.all_orders_delete_success_message),


            Toast.LENGTH_SHORT
        ).show()

        getMyOrdersList()

    }

    fun deleteAllOrders(userId: String) {

        showAlertDialogToDeleteAllOrders(userId)
    }

    private fun showAlertDialogToDeleteAllOrders(userId: String) {
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

                FirestoreClass().deleteAllOrders(this, userId)
                dialog.dismiss()
            }
            .show()
    }

    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {
        hideProgressDialog()
        if (ordersList.size > 0) {
            binding.rvMyOrderItems.visibility = View.VISIBLE
            binding.tvNoOrdersFound.visibility = View.GONE

            binding.rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
            binding.rvMyOrderItems.setHasFixedSize(true)
            binding.rvMyOrderItems.adapter =
                MyOrdersListAdapter(requireActivity(), ordersList, this)


            val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    showProgressDialog(resources.getString(R.string.please_wait))

                    FirestoreClass().deleteAllOrders(
                        this@OrdersFragment,
                        ordersList[viewHolder.adapterPosition].id
                    )

                }
            }

            val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
            deleteItemTouchHelper.attachToRecyclerView(binding.rvMyOrderItems)
        } else {
            binding.rvMyOrderItems.visibility = View.GONE
            binding.tvNoOrdersFound.visibility = View.VISIBLE

        }


    }

    private fun getMyOrdersList() {
        //show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getMyOrdersList(this@OrdersFragment)
    }

   /* override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_all_orders_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_all_orders -> {
                showAlertDialogToDeleteAllOrders(mUserId)
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }*/


    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
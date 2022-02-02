package com.ebenezer.gana.shoppy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.FragmentProductsBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.Products
import com.ebenezer.gana.shoppy.ui.activities.AddProductActivity
import com.ebenezer.gana.shoppy.ui.adapters.MyProductsListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProductsFragment : BaseFragment() {

    //private lateInit var homeViewModel: HomeViewModel
    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentProductsBinding.inflate(inflater, container, false)


        return binding!!.root
    }

    fun deleteProduct(productId: String) {
        showAlertDialogToDeleteProduct(productId)
    }

    /**
     * A function to show the alert dialog for the confirmation of delete product from cloud firestore.
     */
    private fun showAlertDialogToDeleteProduct(productID: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.delete_dialog_title))
            .setMessage(resources.getString(R.string.delete_dialog_message))
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
                com.ebenezer.gana.shoppy.firestore.FirestoreClass().deleteProduct(this, productID)
                dialog.dismiss()
            }
            .show()


        /*val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            //show progress dialog to do the actual deletion from the cloud
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteProduct(this, productID)
            dialogInterface.dismiss()

      }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    */
    }


    fun productDeleteSuccess() {
        //hide the progress dialog, first shown in the alertDialog
        hideProgressDialog()

        Toast.makeText(
            requireActivity(), resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT
        ).show()

        getProductListFromFireStore()


    }

    fun successProductsListFromFireStore(productsList: ArrayList<Products>) {
        hideProgressDialog()
        /*for (item in productsList){
            Log.i("Product Name", item.title)
        }*/

        binding?.let {
            if (productsList.size > 0) {
                binding!!.rvMyProductsItem.visibility = View.VISIBLE
                binding!!.tvNoProductsFound.visibility = View.GONE

                with(binding!!.rvMyProductsItem) {
                    layoutManager = LinearLayoutManager(activity)
                    setHasFixedSize(true)
                    val adapterProducts = MyProductsListAdapter(
                        requireActivity(), productsList,
                        this@ProductsFragment
                    )
                    adapter = adapterProducts
                }
            } else {
                binding!!.rvMyProductsItem.visibility = View.GONE
                binding!!.tvNoProductsFound.visibility = View.VISIBLE
            }
        }


    }

    private fun getProductListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this@ProductsFragment)

    }


    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_products -> {

                startActivity(Intent(activity, AddProductActivity::class.java))

                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
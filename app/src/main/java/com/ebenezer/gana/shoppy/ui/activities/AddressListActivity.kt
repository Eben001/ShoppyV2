package com.ebenezer.gana.shoppy.ui.activities

import android.app.Activity
import android.content.Intent

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.ActivityAddressListBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.Address
import com.ebenezer.gana.shoppy.ui.adapters.AddressListAdapter
import com.ebenezer.gana.shoppy.utils.Constants
import com.ebenezer.gana.shoppy.utils.SwipeToDeleteCallback
import com.ebenezer.gana.shoppy.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {
    lateinit var binding: ActivityAddressListBinding

    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_address_list)

        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        getAddressList()

        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)

        }

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        if (mSelectAddress) {
            binding.tvTitle.text = resources.getString(R.string.title_select_address)
            binding.tvAddAddress.visibility = View.VISIBLE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getAddressList()
        }
    }

    /* override fun onResume() {
         super.onResume()
         getAddressList()
     }*/

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this,
            resources.getString(R.string.err_your_address_deleted_successfully), Toast.LENGTH_SHORT
        ).show()
        getAddressList()

    }

    private fun getAddressList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressesList(this)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {
        hideProgressDialog()
        if (addressList.size > 0) {
            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvNoAddressFound.visibility = View.GONE


            binding.rvAddressList.layoutManager = LinearLayoutManager(this@AddressListActivity)
            binding.rvAddressList.setHasFixedSize(true)
            binding.rvAddressList.adapter =
                AddressListAdapter(this@AddressListActivity, addressList, mSelectAddress)


            if (!mSelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this@AddressListActivity) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = binding.rvAddressList.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)


                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )

                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)

            }


        } else {
            //hideProgressDialog()
            binding.rvAddressList.visibility = View.GONE
            binding.tvNoAddressFound.visibility = View.VISIBLE
        }


    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddressListActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddressListActivity.setNavigationOnClickListener { onBackPressed() }

    }
}
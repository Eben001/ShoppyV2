package com.ebenezer.gana.shoppyv2.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.databinding.ListItemAddressBinding
import com.ebenezer.gana.shoppyv2.models.Address
import com.ebenezer.gana.shoppyv2.ui.activities.AddEditAddressActivity
import com.ebenezer.gana.shoppyv2.ui.activities.CheckoutActivity
import com.ebenezer.gana.shoppyv2.utils.Constants

class AddressListAdapter(
    private val context: Context,
    private var addressList: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {


    inner class ViewHolder(val binding:ListItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        private lateinit var address: Address

        fun bind(address: Address) {
            this.address = address
            binding.tvAddressFullName.text = address.name
            binding.tvAddressType.text = address.type
            binding.tvAddressDetails.text =
                "${address.address}, ${address.zipCode}"
            binding.tvAddressMobileNumber.text =
                address.mobileNumber

            if (selectAddress) {
                itemView.setOnClickListener {
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, address)

                    context.startActivity(intent)

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ListItemAddressBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)

    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, addressList[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(addressList[position])
    }

    override fun getItemCount() = addressList.size
}
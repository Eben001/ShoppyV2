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
import com.ebenezer.gana.shoppyv2.models.Address
import com.ebenezer.gana.shoppyv2.ui.activities.AddEditAddressActivity
import com.ebenezer.gana.shoppyv2.ui.activities.CheckoutActivity
import com.ebenezer.gana.shoppyv2.utils.Constants

class AddressListAdapter(
    private val context: Context,
    private var addressList: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var address: Address

        fun bind(address: Address) {
            this.address = address
            itemView.findViewById<TextView>(R.id.tv_address_full_name).text = address.name
            itemView.findViewById<TextView>(R.id.tv_address_type).text = address.type
            itemView.findViewById<TextView>(R.id.tv_address_details).text =
                "${address.address}, ${address.zipCode}"
            itemView.findViewById<TextView>(R.id.tv_address_mobile_number).text =
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

        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_address,
                parent, false
            )
        )
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
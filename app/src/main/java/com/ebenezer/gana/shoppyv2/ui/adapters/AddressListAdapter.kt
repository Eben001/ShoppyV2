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
    private val selectAddress:Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return AddressViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_address,
                parent, false
            )
        )
    }

    fun notifyEditItem(activity: Activity, position:Int){
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, addressList[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = addressList[position]

        if (holder is AddressViewHolder){
            holder.itemView.findViewById<TextView>(R.id.tv_address_full_name).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tv_address_type).text = model.type
            holder.itemView.findViewById<TextView>(R.id.tv_address_details).text = "${model.address}, ${model.zipCode}"
            holder.itemView.findViewById<TextView>(R.id.tv_address_mobile_number).text = model.mobileNumber

            if (selectAddress){
                holder.itemView.setOnClickListener {
                   /* Toast.makeText(context,
                    "Selected address: ${model.address}, ${model.zipCode}",
                    Toast.LENGTH_SHORT).show()*/
                    val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)

                    context.startActivity(intent)

                }
            }
        }

    }
    override fun getItemCount(): Int {
        return addressList.size
    }
}
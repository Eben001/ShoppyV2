package com.ebenezer.gana.shoppy.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.models.SoldProduct
import com.ebenezer.gana.shoppy.ui.activities.SoldProductsDetailsActivity
import com.ebenezer.gana.shoppy.utils.Constants

import com.ebenezer.gana.shoppy.utils.GlideLoader

class SoldProductListAdapter(
    private val context: Context,
    private val soldProductList: ArrayList<SoldProduct>
) :  RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_product,
                parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = soldProductList[position]

        if (holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.findViewById(R.id.iv_item_image)
            )

            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_item_price).text  = "₦${model.total_amount}"
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).visibility = View.GONE

            holder.itemView.setOnClickListener {
                val intent = Intent(context, SoldProductsDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCTS_DETAILS, model)
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return soldProductList.size

    }
}
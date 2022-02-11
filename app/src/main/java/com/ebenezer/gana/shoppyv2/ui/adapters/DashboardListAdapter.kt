package com.ebenezer.gana.shoppyv2.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.models.Products
import com.ebenezer.gana.shoppyv2.ui.activities.ProductDetailsActivity
import com.ebenezer.gana.shoppyv2.utils.Constants
import com.ebenezer.gana.shoppyv2.utils.GlideLoader

class DashboardListAdapter(
    private val context: Context,
    private var allProducts: ArrayList<Products>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //private var onClickListener: OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }

   /* fun setOnclickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }*/



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = allProducts[position]
        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.findViewById(R.id.iv_dashboard_item_image)
            )
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_price).text =
                "₦${model.price}"
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_description).text =
                model.description

            holder.itemView.setOnClickListener{
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)

                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id) // the id of the user who uploaded the products
                context.startActivity(intent)
            }

            /* holder.itemView.setOnClickListener{
                 onClickListener.let {
                     it!!.onClick(position, model)
                 }
             }*/
        }
    }

    override fun getItemCount(): Int {
        return allProducts.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /*interface  OnClickListener{
        fun onClick(position:Int, product:Products)
    }*/
}


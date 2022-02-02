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
import com.ebenezer.gana.shoppy.models.Order
import com.ebenezer.gana.shoppy.ui.activities.MyOrderDetailsActivity
import com.ebenezer.gana.shoppy.ui.fragments.OrdersFragment
import com.ebenezer.gana.shoppy.ui.fragments.ProductsFragment
import com.ebenezer.gana.shoppy.utils.Constants
import com.ebenezer.gana.shoppy.utils.GlideLoader

class MyOrdersListAdapter(
    private val context: Context,
    private val ordersList: ArrayList<Order>,
    private val fragment: OrdersFragment,
    private val userId:String

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_product,
            parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = ordersList[position]

        if (holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.findViewById(R.id.iv_item_image)
            )

            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_item_price).text  = "₦${model.total_amount}"
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).visibility = View.VISIBLE


            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).setOnClickListener {
                fragment.deleteAllOrders(model.id)
            }


            holder.itemView.setOnClickListener {
                val intent = Intent(context, MyOrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_MY_ODER_DETAILS, model)
                context.startActivity(intent)
            }

        }
    }

    override fun getItemCount(): Int {
        return ordersList.size

    }
}
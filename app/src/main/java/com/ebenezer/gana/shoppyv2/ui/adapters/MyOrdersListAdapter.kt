package com.ebenezer.gana.shoppyv2.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.models.Order
import com.ebenezer.gana.shoppyv2.ui.activities.MyOrderDetailsActivity
import com.ebenezer.gana.shoppyv2.ui.fragments.OrdersFragment
import com.ebenezer.gana.shoppyv2.utils.Constants
import com.ebenezer.gana.shoppyv2.utils.GlideLoader

class MyOrdersListAdapter(
    private val context: Context,
    private val ordersList: ArrayList<Order>,
    private val fragment: OrdersFragment

) :
    RecyclerView.Adapter<MyOrdersListAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var orders: Order
        fun bind(orders: Order) {
            this.orders = orders

            GlideLoader(context).loadProductPicture(
                orders.image,
                itemView.findViewById(R.id.iv_item_image)
            )

            itemView.findViewById<TextView>(R.id.tv_item_name).text = orders.title
            itemView.findViewById<TextView>(R.id.tv_item_price).text = "₦${orders.total_amount}"
            itemView.findViewById<ImageButton>(R.id.ib_delete_product).visibility = View.VISIBLE

            itemView.findViewById<ImageButton>(R.id.ib_delete_product).setOnClickListener {
                fragment.deleteAllOrders(orders.id)
            }


            itemView.setOnClickListener {
                val intent = Intent(context, MyOrderDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS, orders)
                context.startActivity(intent)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_product,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ordersList[position])
    }

    override fun getItemCount() = ordersList.size
}
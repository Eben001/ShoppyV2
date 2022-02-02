package com.ebenezer.gana.shoppy.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.models.Products
import com.ebenezer.gana.shoppy.ui.activities.ProductDetailsActivity
import com.ebenezer.gana.shoppy.ui.fragments.ProductsFragment
import com.ebenezer.gana.shoppy.utils.Constants
import com.ebenezer.gana.shoppy.utils.GlideLoader

open class MyProductsListAdapter(
    private val context: Context,
    private var list:ArrayList<Products>,
    private val fragment: ProductsFragment
): RecyclerView.Adapter<RecyclerView.ViewHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_product,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder){
            GlideLoader(context).loadProductPicture(model.image, holder.itemView.findViewById(R.id.iv_item_image))
            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_item_price).text = "₦${model.price}"
            holder.itemView.findViewById<TextView>(R.id.tv_item_description).text = model.description

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product).setOnClickListener{

                fragment.deleteProduct(model.product_id)
            }
            holder.itemView.setOnClickListener{
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)

                context.startActivity(intent)
            }



        }
    }
    override fun getItemCount(): Int {
        return list.size

    }

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)


}
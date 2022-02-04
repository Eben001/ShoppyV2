package com.ebenezer.gana.shoppy.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.CartItem
import com.ebenezer.gana.shoppy.ui.activities.CartListActivity
import com.ebenezer.gana.shoppy.utils.Constants
import com.ebenezer.gana.shoppy.utils.GlideLoader

class CartListAdapter(
    private val context: Context,
    private var cartListItems: ArrayList<CartItem>,
    private val updateCartItems:Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CartViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_cart,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = cartListItems[position]
        if (holder is CartViewHolder) {
            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.findViewById(R.id.iv_cart_item_image)
            )
            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_price).text = "₦${model.price}"
            holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text = model.cart_quantity


            if (model.cart_quantity == "0") {
                holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility =
                    View.GONE
                holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility =
                    View.GONE


                if (updateCartItems){
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item)
                        .visibility = View.VISIBLE
                }else{
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item)
                        .visibility = View.GONE
                }



                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text =
                    context.resources.getString(R.string.lb_out_of_stock)

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorSnackBarError
                    )
                )

            } else {

                if (updateCartItems){
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item)
                        .visibility = View.VISIBLE

                }else{
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility =
                        View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility =
                        View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item)
                        .visibility = View.GONE

                }



                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorSecondaryText
                    )
                )

            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item).setOnClickListener {
                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))

                    }
                }
                FirestoreClass()
                    .removedItemFromCart(context, model.id)
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).setOnClickListener {
                if (model.cart_quantity == "1") {
                    FirestoreClass()
                        .removedItemFromCart(context, model.id)
                } else {
                    val cartQuantity: Int = model.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    // show the progress dialog.

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))

                    }

                    FirestoreClass()
                        .updateMyCart(context, model.id, itemHashMap)
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).setOnClickListener {
                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()) {
                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    // show the progress dialog.
                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))

                    }

                    FirestoreClass()
                        .updateMyCart(context, model.id, itemHashMap)


                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(
                                R.string.msg_for_available_stock,
                                model.stock_quantity
                            ), true
                        )
                    }
                }


            }


        }

    }

    override fun getItemCount(): Int {

        return cartListItems.size
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
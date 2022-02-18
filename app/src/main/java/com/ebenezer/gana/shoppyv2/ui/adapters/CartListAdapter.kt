package com.ebenezer.gana.shoppyv2.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.databinding.ListItemCartBinding
import com.ebenezer.gana.shoppyv2.firestore.FirestoreClass
import com.ebenezer.gana.shoppyv2.models.CartItem
import com.ebenezer.gana.shoppyv2.ui.activities.CartListActivity
import com.ebenezer.gana.shoppyv2.utils.Constants
import com.ebenezer.gana.shoppyv2.utils.GlideLoader

class CartListAdapter(
    private val context: Context,
    private var cartListItems: ArrayList<CartItem>,
    private val updateCartItems: Boolean
) : RecyclerView.Adapter<CartListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            ListItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartListItems[position])

    }

    override fun getItemCount(): Int {

        return cartListItems.size
    }

    inner class ViewHolder(val binding: ListItemCartBinding) : View.OnClickListener,

        RecyclerView.ViewHolder(binding.root) {
        private lateinit var cartItem: CartItem

        fun bind(cartItem: CartItem) {
            this.cartItem = cartItem

            GlideLoader(context).loadProductPicture(
                cartItem.image,
                binding.ivCartItemImage
            )


            binding.tvCartItemTitle.text = cartItem.title
            binding.tvCartItemPrice.text = "₦${cartItem.price}"
            binding.tvCartQuantity.text = cartItem.cart_quantity


            binding.ibDeleteCartItem.setOnClickListener(this)
            binding.ibAddCartItem.setOnClickListener(this)
            binding.ibRemoveCartItem.setOnClickListener(this)

            if (cartItem.cart_quantity == "0") {
                binding.ibRemoveCartItem.visibility =
                    View.GONE
                binding.ibAddCartItem.visibility =
                    View.GONE


                if (updateCartItems) {
                    binding.ibDeleteCartItem
                        .visibility = View.VISIBLE
                } else {
                    binding.ibDeleteCartItem
                        .visibility = View.GONE
                }

                binding.tvCartQuantity.text =
                    context.resources.getString(R.string.lb_out_of_stock)

                binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorSnackBarError
                    )
                )

            } else {

                if (updateCartItems) {
                    binding.ibRemoveCartItem.visibility =
                        View.VISIBLE
                    binding.ibAddCartItem.visibility =
                        View.VISIBLE
                    binding.ibDeleteCartItem
                        .visibility = View.VISIBLE

                } else {
                    binding.ibRemoveCartItem.visibility =
                        View.GONE
                    binding.ibAddCartItem.visibility =
                        View.GONE
                    binding.ibDeleteCartItem
                        .visibility = View.GONE

                }

                binding.tvCartQuantity.setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorSecondaryText
                    )
                )

            }

        }

        override fun onClick(v: View?) {
            if (v != null) {
                when (v.id) {
                    R.id.ib_delete_cart_item -> {
                        when (context) {
                            is CartListActivity -> {
                                context.showProgressDialog(context.resources.getString(R.string.please_wait))

                            }
                        }
                        FirestoreClass().removedItemFromCart(context, cartItem.id)
                    }
                    R.id.ib_remove_cart_item -> {
                        if (cartItem.cart_quantity == "1") {
                            FirestoreClass()
                                .removedItemFromCart(context, cartItem.id)
                        } else {
                            val cartQuantity: Int = cartItem.cart_quantity.toInt()

                            val itemHashMap = HashMap<String, Any>()

                            itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                            // show the progress dialog.

                            if (context is CartListActivity) {
                                context.showProgressDialog(context.resources.getString(R.string.please_wait))

                            }

                            FirestoreClass()
                                .updateMyCart(context, cartItem.id, itemHashMap)
                        }
                    }
                    R.id.ib_add_cart_item -> {
                        val cartQuantity: Int = cartItem.cart_quantity.toInt()

                        if (cartQuantity < cartItem.stock_quantity.toInt()) {
                            val itemHashMap = HashMap<String, Any>()

                            itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                            // show the progress dialog.
                            if (context is CartListActivity) {
                                context.showProgressDialog(context.resources.getString(R.string.please_wait))

                            }

                            FirestoreClass().updateMyCart(context, cartItem.id, itemHashMap)


                        } else {
                            if (context is CartListActivity) {
                                context.showErrorSnackBar(
                                    context.resources.getString(
                                        R.string.msg_for_available_stock,
                                        cartItem.stock_quantity
                                    ), true
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}
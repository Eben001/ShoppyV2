package com.ebenezer.gana.shoppy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.ActivityProductDetailsBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.CartItem
import com.ebenezer.gana.shoppy.models.Products
import com.ebenezer.gana.shoppy.utils.Constants
import com.ebenezer.gana.shoppy.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityProductDetailsBinding

    private var mProductId: String = ""

    private lateinit var mProductDetails: Products
    private var mProductOwnerId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_product_details)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!

        }
        // Get the product owner id through intent.
        //var productOwnerId: String = ""
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!

        }


        // Now we have the product owner id so if the product which is added by owner himself should not see the button Add To Cart.
        if (FirestoreClass().getCurrentUserId() == mProductOwnerId
        ) {
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        } else {
            binding.btnAddToCart.visibility = View.VISIBLE
        }

        getProductDetails()
        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)
    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this, mProductId)
    }

    fun productExistInCart() {
        hideProgressDialog()
        // if the product already exist in the cart, hide the add to cart button
        // and make go to cart button visible
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE

    }


    fun productDetailsSuccess(product: Products) {
        mProductDetails = product
        //hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            binding.ivProductDetailImage
        )
        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "₦${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableQuantity.text = product.stock_quantity
        binding.tvProductDetailsShippingCharge.text = "₦${product.shipping_charge}"

        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsAvailableQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )

            )
            binding.tvProductDetailsAvailableQuantity.text =
                resources.getString(R.string.lb_out_of_stock)


        } else {

            if (FirestoreClass().getCurrentUserId() == product.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistsInCart(this, mProductId)
            }
        }


    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarProductDetailsActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }

    }

    /**
     * A function to prepare the cart item to add it to the cart in cloud firestore.
     */

    private fun addToCart() {
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserId(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY,
            "", "", mProductDetails.shipping_charge
        )

        //show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this, cartItem)
    }

    //  Notify the success result of item added to the to cart.
    fun addToCartSuccess() {
        //hide progress dialog, first show when addToCart() is called
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        // After adding to cart, make the button invisible and
        // make go to cart button visible
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE


    }


    override fun onClick(v: View?) {
        v.let {
            when (it!!.id) {
                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }
}
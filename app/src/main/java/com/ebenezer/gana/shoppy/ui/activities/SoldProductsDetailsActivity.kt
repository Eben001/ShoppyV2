package com.ebenezer.gana.shoppy.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.ActivitySoldProductsDetailsBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.SoldProduct
import com.ebenezer.gana.shoppy.utils.Constants
import com.ebenezer.gana.shoppy.utils.Constants.PAYMENT_STATUS
import com.ebenezer.gana.shoppy.utils.GlideLoader
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class SoldProductsDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivitySoldProductsDetailsBinding

    //private var mSoldProductOwner: String = ""
    private var mProductDetails:SoldProduct = SoldProduct()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sold_products_details)

        binding = ActivitySoldProductsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

//
//        var productDetails: SoldProduct = SoldProduct()

        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCTS_DETAILS)) {
            mProductDetails =
                intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCTS_DETAILS)!!

        }
        setupUI(mProductDetails)

    }


    private fun setupUI(productDetails: SoldProduct) {
        binding.tvSoldProductDetailsId.text = productDetails.order_id

        val dateFormat = "dd MMM yyyy HH:mm"

        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())


        val calender: Calendar = Calendar.getInstance()
        calender.timeInMillis = productDetails.order_date
        binding.tvSoldProductDetailsDate.text = formatter.format(calender.time)

        GlideLoader(this@SoldProductsDetailsActivity).loadProductPicture(
            productDetails.image,
            binding.ivProductItemImage
        )

        binding.tvProductItemName.text = productDetails.title
        binding.tvProductItemPrice.text = "${productDetails.price}"

        binding.tvSoldDetailsAddressType.text = productDetails.address.type
        binding.tvSoldDetailsFullName.text = productDetails.address.name
        binding.tvSoldDetailsAddress.text =
            "${productDetails.address.address}, ${productDetails.address.zipCode}"

        binding.tvCartQuantity.text = productDetails.sold_quantity
        binding.tvSoldDetailsAdditionalNote.text = productDetails.address.additionalNote


        if (productDetails.address.otherDetails.isNotEmpty()) {
            binding.tvSoldDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvSoldDetailsOtherDetails.text = productDetails.address.otherDetails
        } else {
            binding.tvSoldDetailsOtherDetails.visibility = View.GONE

        }

        binding.tvSoldDetailsMobileNumber.text = productDetails.address.mobileNumber
        binding.tvSoldProductSubTotal.text = "₦${productDetails.sub_total_amount}"
        binding.tvSoldProductShippingCharge.text = "₦${productDetails.shipping_charge}"
        binding.tvSoldProductTotalAmount.text = "₦${productDetails.total_amount}"


    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSoldProductDetailsActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarSoldProductDetailsActivity.setNavigationOnClickListener { onBackPressed() }

    }




}
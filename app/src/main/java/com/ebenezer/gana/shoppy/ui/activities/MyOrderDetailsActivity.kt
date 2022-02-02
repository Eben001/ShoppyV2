package com.ebenezer.gana.shoppy.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.ActivityMyOrderDetailsBinding
import com.ebenezer.gana.shoppy.models.Order
import com.ebenezer.gana.shoppy.ui.adapters.CartListAdapter
import com.ebenezer.gana.shoppy.utils.Constants
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_my_order_details)

        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()


        var myOrderDetails: Order = Order()

        if (intent.hasExtra(Constants.EXTRA_MY_ODER_DETAILS)) {
            myOrderDetails = intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ODER_DETAILS)!!
        }


        setupUI(myOrderDetails)

    }


    private fun setupUI(orderDetails: Order) {
        binding.tvOrderDetailsId.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        binding.tvOrderDetailsDate.text = orderDateTime

        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime

        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")


        when {
            diffInHours < 1 -> {
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_pending)
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorAccent
                    )
                )
            }

            diffInHours < 2 -> {
                binding.tvOrderStatus.text = resources.getString(R.string.order_status_in_process)
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
            }
            else -> {

                binding.tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
                binding.tvOrderStatus.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )

            }

        }


        binding.rvMyOrderItemsList.apply {
            layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
            setHasFixedSize(true)

            //reuse the cartAdapter
            adapter = CartListAdapter(this@MyOrderDetailsActivity,
            orderDetails.items, false )

        }
        binding.tvMyOrderDetailsAddressType.text = orderDetails.address.type
        binding.tvMyOrderDetailsFullName.text = orderDetails.address.name
        binding.tvMyOrderDetailsAddress.text =
            "${orderDetails.address.address}, ${orderDetails.address.zipCode}"

        binding.tvMyOrderDetailsAdditionalNote.text = orderDetails.address
            .additionalNote


        if (orderDetails.address.otherDetails.isNotEmpty()){
            binding.tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
            binding.tvMyOrderDetailsOtherDetails.text = orderDetails.address.otherDetails

        }else{
            binding.tvMyOrderDetailsOtherDetails.visibility = View.GONE

        }

        binding.tvMyOrderDetailsMobileNumber.text = orderDetails.address.mobileNumber
        binding.tvOrderDetailsSubTotal.text = orderDetails.sub_total_amount
        binding.tvOrderDetailsShippingCharge.text = orderDetails.shipping_charge
        binding.tvOrderDetailsTotalAmount.text = orderDetails.total_amount


    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMyOrderDetailsActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarMyOrderDetailsActivity.setNavigationOnClickListener { onBackPressed() }

    }
}
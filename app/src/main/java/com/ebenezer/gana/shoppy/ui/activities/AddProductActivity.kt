package com.ebenezer.gana.shoppy.ui.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.ActivityAddProductBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.Products
import com.ebenezer.gana.shoppy.utils.Constants
import com.ebenezer.gana.shoppy.utils.GlideLoader
import java.io.IOException

const val TAG = "AddProductActivity"

class AddProductActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityAddProductBinding

    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_add_product)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupActionBar()

        binding.ivProductImage.setOnClickListener(this)
        binding.btnAddProduct.setOnClickListener(this)

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddProductActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.iv_product_image -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddProductActivity)
                    } else {
                        /* Request permission to be granted to this application. This permission must
                           requested in the manifest, they should not be granted to your app and
                           they should not have protection level  */
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_add_product -> {
                    if (validateProductDetails()) {
                        uploadProductImage()
                    }

                }

            }
        }
    }

    private fun uploadProductImage() {
        //show progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(
            this,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )
    }
    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this@AddProductActivity,
        resources.getString(R.string.product_uploaded_success),
        Toast.LENGTH_SHORT).show()

        finish()
    }

    fun imageUploadSuccess(imageURL: String) {
//        hideProgressDialog()
//        showErrorSnackBar("Product Image is uploaded successfully $imageURL", false)

        mProductImageURL = imageURL

        uploadProductDetails()
    }

    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(Constants.MYSHOPPAL_PREFERENCES,
            Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME, "")!!

        val product = Products(
            FirestoreClass().getCurrentUserId(),
            user_name = username,
            binding.etProductTitle.text.toString().trim{it <= ' '},
            binding.etProductPrice.text.toString().trim{it <= ' '},
            binding.etProductDescription.text.toString().trim{it <= ' '},
            binding.etProductQuantity.text.toString().trim{it <= ' '},
            mProductImageURL, "", binding.etProductShippingCharge.text.toString().trim{it <= ' '}
        )

        FirestoreClass().uploadProductDetails(this@AddProductActivity, productInfo = product)
    }

    private fun validateProductDetails(): Boolean {
        return when {
            mSelectedImageFileUri == null -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_select_product_image),
                    errorMessage = true
                )
                false
            }
            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }
            TextUtils.isEmpty(binding.etProductQuantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }

            else -> {
                true
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@AddProductActivity)
            }
        } else {
            Toast.makeText(
                this@AddProductActivity,
                resources.getString(R.string.read_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                data?.let {
                    binding.ivAddUpdateProduct.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vector_edit
                        )
                    )
                    try {
                        mSelectedImageFileUri = it.data

                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            binding.ivProductImage
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddProductActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e(TAG, "onActivityResult: Image Selection failed")
        }
    }


}
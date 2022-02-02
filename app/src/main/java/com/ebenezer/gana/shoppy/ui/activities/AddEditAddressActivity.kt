package com.ebenezer.gana.shoppy.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.ActivityAddEditAddressBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.Address
import com.ebenezer.gana.shoppy.utils.Constants

class AddEditAddressActivity : BaseActivity() {

    lateinit var binding: ActivityAddEditAddressBinding
    private var mAddressDetails: Address? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_add_edit_address)

        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()


        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {
                binding.tvTitle.text = resources.getString(R.string.title_edit_address)
                binding.btnSubmitAddress.text = resources.getString(R.string.btn_lbl_update)
                binding.etFullName.setText(mAddressDetails?.name)
                binding.etPhoneNumber.setText(mAddressDetails?.mobileNumber)
                binding.etAddress.setText(mAddressDetails?.address)
                binding.etZipCode.setText(mAddressDetails?.zipCode)
                binding.etAdditionalNote.setText(mAddressDetails?.additionalNote)

                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        binding.rbHome.isChecked = true
                    }

                    Constants.OFFICE -> {
                        binding.rbOffice.isChecked = true
                    }

                    else -> {
                        binding.rbOther.isChecked = true

                        binding.tilOtherDetails.visibility = View.VISIBLE
                        binding.etOtherDetails.setText(mAddressDetails?.otherDetails)
                    }
                }

            }
        }


        binding.rgType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                binding.tilOtherDetails.visibility = View.VISIBLE
            } else {
                binding.tilOtherDetails.visibility = View.GONE
            }

        }

        binding.btnSubmitAddress.setOnClickListener {
            saveAddressToFirestore()
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage =
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                resources.getString(R.string.err_your_address_updated_successfully)
            } else {
                resources.getString(R.string.err_address_added_success)
            }

        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()

        setResult(RESULT_OK)

        finish()
    }

    private fun saveAddressToFirestore() {
        val fullName: String = binding.etFullName.text.toString().trim { it <= ' ' }
        val phoneNumber: String = binding.etPhoneNumber.text.toString().trim { it <= ' ' }

        val address: String = binding.etAddress.text.toString().trim { it <= ' ' }

        val zipCode: String = binding.etZipCode.text.toString().trim { it <= ' ' }

        val additionalNote: String = binding.etAdditionalNote.text.toString().trim { it <= ' ' }

        val otherDetails: String = binding.etOtherDetails.text.toString().trim { it <= ' ' }


        if (validateData()) {
            // show the progress dialog
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                binding.rbHome.isChecked -> {
                    Constants.HOME

                }
                binding.rbOffice.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            // only update the address in the Firestore when it's not empty
            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
               FirestoreClass().updateAddress(
                    this,
                    addressModel, mAddressDetails!!.id
                )
            } else {
                FirestoreClass().addAddress(this@AddEditAddressActivity, addressModel)

            }


        }


    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(

                binding.etFullName.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    errorMessage = true
                )
                false
            }
            TextUtils.isEmpty(
                binding.etPhoneNumber.text.toString()
                    .trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    errorMessage = true
                )
                false
            }

            TextUtils.isEmpty(binding.etAddress.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_address),
                    errorMessage = true
                )
                false
            }
            TextUtils.isEmpty(binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_zip_code),
                    errorMessage = true
                )
                false
            }
            binding.rbOther.isChecked && TextUtils.isEmpty(
                binding.etZipCode.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_other_details),
                    errorMessage = true
                )
                false
            }
            else -> {
                true
            }
        }
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddEditAddressActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarAddEditAddressActivity.setNavigationOnClickListener { onBackPressed() }

    }
}
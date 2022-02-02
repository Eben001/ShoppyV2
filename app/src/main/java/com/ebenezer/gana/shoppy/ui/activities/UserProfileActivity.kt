package com.ebenezer.gana.shoppy.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.ui.setupActionBarWithNavController
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.ActivityUserProfileBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.User
import com.ebenezer.gana.shoppy.utils.Constants
import com.ebenezer.gana.shoppy.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageUrl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_user_profile)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.ivUserPhoto.setOnClickListener(this@UserProfileActivity)
        binding.btnSubmit.setOnClickListener(this@UserProfileActivity)


        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the extra user details from intent as a ParcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        binding.etFirstName.setText(mUserDetails.firstName)
        binding.etLastName.setText(mUserDetails.lastName)
        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mUserDetails.email)


        if (mUserDetails.profileCompleted == 0) {
            binding.tvTitle.text = resources.getString(R.string.title_complete_profile)

            binding.etFirstName.isEnabled = false
            binding.etLastName.isEnabled = false


        } else {
            setupActionBar()
            binding.tvTitle.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(
                mUserDetails.image,
                binding.ivUserPhoto
            )

            if (mUserDetails.mobile != 0L) {
                binding.etMobileNumber.setText(mUserDetails.mobile.toString())

            }
            if (mUserDetails.gender == Constants.MALE) {
                binding.rbMale.isChecked = true
            } else {
                binding.rbFemale.isChecked = true
            }

        }


    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarUserProfileActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    // We check if the permission is already granted.
                    // We check first check READ_EXTERNAL_STORAGE permission
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        /*showErrorSnackBar(
                            resources.getString(R.string.you_already_have_access_to_storage_text),
                            errorMessage = false
                        )*/

                        // Display image chooser
                        Constants.showImageChooser(this@UserProfileActivity)
                    } else {
                        /* Request permission to be granted to this application. These permission
                            must be requested in the manifest, they should not be granted to your app
                         */
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {

                    if (validateUserProfileDetails()) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(
                                this,
                                mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE
                            )
                        } else {
                            updateUserProfileUserDetails()
                        }
                    }
                }


            }
        }
    }


    private fun validateUserProfileDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_phone_number), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun updateUserProfileUserDetails() {
        val userHashMap = HashMap<String, Any>()

        val firstName = binding.etFirstName.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }
        val lastName = binding.etLastName.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName

        }


        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }

        val gender = if (binding.rbMale.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mUserProfileImageUrl.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageUrl
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            //key:mobile, value: mobileNumber
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        //key:gender, value:male
        //key:gender, value:female
        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1


        FirestoreClass().updateUserProfileData(this, userHashMap = userHashMap)

    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()

    }


    fun imageUploadSuccess(imageURL: String) {
        //hideProgressDialog()
        mUserProfileImageUrl = imageURL
        updateUserProfileUserDetails()

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*  showErrorSnackBar(
                      resources.getString(R.string.read_permission_granted),
                      errorMessage = false
                  )*/
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                Toast.makeText(
                    this@UserProfileActivity,
                    resources.getString(R.string.read_permission_denied),

                    Toast.LENGTH_LONG
                ).show()

            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {

                if (data != null) {
                    try {
                        // The uri of selected image from phone
                        mSelectedImageFileUri = data.data!!
                        //binding.ivUserPhoto.setImageURI(selectedImageFileUri)
                        //binding.ivUserPhoto.setImageURI(it.data)
                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            binding.ivUserPhoto
                        )

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancel")
        }

    }

}
package com.ebenezer.gana.shoppy.ui.activities

import android.content.Intent
import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.databinding.ActivitySettingsBinding
import com.ebenezer.gana.shoppy.firestore.FirestoreClass
import com.ebenezer.gana.shoppy.models.User
import com.ebenezer.gana.shoppy.utils.Constants
import com.ebenezer.gana.shoppy.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var mUserDetails : User



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupActionBar()

        binding.tvEdit.setOnClickListener(this)
        binding.btnLogout.setOnClickListener(this)
        binding.llAddress.setOnClickListener(this)



    }



    override fun onClick(v: View?) {
        v?.let{
            when(v.id){

                R.id.tv_edit ->{
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java )
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)

                }


                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.ll_address ->{
                    startActivity(Intent(this@SettingsActivity, AddressListActivity::class.java))

                }
            }
        }
    }



    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSettingsActivity)
        val actionbar = supportActionBar
        actionbar?.let {
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        binding.toolbarSettingsActivity.setNavigationOnClickListener { onBackPressed() }

    }
    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user


        hideProgressDialog()

        GlideLoader(this@SettingsActivity).loadUserPicture(
            user.image,
            binding.ivUserPhoto
        )
        binding.tvName.text = "${user.firstName} ${user.lastName}"
        binding.tvGender.text = user.gender
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()

    }


}
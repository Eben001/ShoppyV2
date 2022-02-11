package com.ebenezer.gana.shoppyv2.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.ebenezer.gana.shoppyv2.R
import com.ebenezer.gana.shoppyv2.utils.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences =
            getSharedPreferences(
                Constants.MYSHOPPAL_PREFERENCES,
                Context.MODE_PRIVATE
            )
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME,
            "")!!

        findViewById<TextView>(R.id.tv_name).text = "Hi there, $username"

    }
}
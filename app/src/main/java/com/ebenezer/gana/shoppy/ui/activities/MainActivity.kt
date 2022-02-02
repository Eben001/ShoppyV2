package com.ebenezer.gana.shoppy.ui.activities

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.ebenezer.gana.shoppy.R
import com.ebenezer.gana.shoppy.utils.Constants

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
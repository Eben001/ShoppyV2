package com.ebenezer.gana.shoppy.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class ShoppyEditText(context: Context, attributeSet: AttributeSet):
AppCompatEditText(context, attributeSet){

    init {
        applyFonts()
    }

    private fun applyFonts(){
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Montserrat-Regular.ttf")
        setTypeface(typeface)
    }
}
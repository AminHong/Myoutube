package com.amin.myoutube.view

import android.content.Context
import android.support.v17.leanback.widget.BaseCardView

class CardView(context: Context?) : BaseCardView(context){

    fun init(){
        // Make sure the LiveCardView is focusable.
        isFocusable = true
        isFocusableInTouchMode = true
        setBackgroundResource(android.R.color.transparent)
    }
}

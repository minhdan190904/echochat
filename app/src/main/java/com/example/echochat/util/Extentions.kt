package com.example.echochat.util

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun View.hide(){
    visibility = View.INVISIBLE
}

fun View.show(){
    visibility = View.VISIBLE
}

fun Fragment.toast(content: String){
    Toast.makeText(context, content, Toast.LENGTH_LONG).show()
}
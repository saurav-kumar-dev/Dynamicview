package com.codingwithsaurav.dynamicview

import android.util.Log
import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

fun TextInputEditText.isEmpty(): Boolean {
    return this.text.toString().trim().isNullOrEmpty()
}

fun TextInputLayout.isError(): Boolean {
    Log.d("jkbkjkjkbkkjkb error false",  "${this} error ${this.error.toString()}")
    if(this.error.toString().trim().isNullOrEmpty()){
        Log.d("jkbkjkjkbkkjkb error true",  "${this} error ${this.error.toString()}")
    }else{
        Log.d("jkbkjkjkbkkjkb error false",  "${this} error ${this.error.toString()}")
    }
    return this.error.toString().trim().isNullOrEmpty()
}

fun AutoCompleteTextView.isEmpty(): Boolean {
    return this.text.toString().trim().isNullOrEmpty()
}

fun TextInputEditText.isNotEmpty(): Boolean {
    return this.text.toString().trim().isNullOrEmpty().not()
}

fun AutoCompleteTextView.isNotEmpty(): Boolean {
    return this.text.toString().trim().isNullOrEmpty().not()
}

fun TextInputEditText.getTimeInMinute(): Int {
    val startParts = this.text.toString().trim().split(":")
    return startParts[0].toInt() * 60 + startParts[1].toInt()
}
fun AutoCompleteTextView.getDurationInMinute(): Int {
    return this.text.toString().trim().replace(" min","").toInt()
}
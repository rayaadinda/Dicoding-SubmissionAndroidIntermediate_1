package com.dicoding.submission1int.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText

class CustomPasswordEditText : EditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        if (text.length < 8) {
            setError("Password must be at least 8 characters")
        }
    }
}
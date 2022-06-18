package com.hyj.permission.demo

import android.content.Context
import android.widget.Toast

/**
 * User: hyj
 * Date: 2022/6/17 23:33
 */

fun Context.showToast(msg: CharSequence) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
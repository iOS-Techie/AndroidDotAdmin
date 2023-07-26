package com.github.drjacky.imagepicker.listener

internal interface ResultListener<T> {

    fun onResult(t: T?)
}

package com.example.nfc.mynfcreader

import okhttp3.Call
import okhttp3.Response
import javax.security.auth.callback.Callback



//fun<T> Call<T>.enqueue(callback: CallbackKt<T>.() -> Unit) {
//    val callbackKt = CallbackKt<T>()
//    callback.invoke(callBackKt)
//    this.enqueue(callBackKt)
//}
//
//class CallbackKt<T>: Callback<T> {
//
//    var onResponse: ((Response<T>) -> Unit)? = null
//    var onFailure: ((t: Throwable?) -> Unit)? = null
//
//    override fun onFailure(call: Call<T>, t: Throwable) {
//        onFailure?.invoke(t)
//    }
//
//    override fun onResponse(call: Call<T>, response: Response<T>) {
//        onResponse?.invoke(response)
//    }
//
//}

//fun Call.enqueue(callback: CallbackKt() -> Unit) {
//    val callbackKt = CallbackKt()
//    callback.invoke(callbackKt)
//    this.enqueue(callbackKt)
//}

//class CallbackKt<T>: Callback {
//
//    var onResponse: ((Response) -> Unit)? = null
//    var onFailure: ((t: Throwable?) -> Unit)? = null
//
//     fun onFailure(call: Call, t: Throwable) {
//        onFailure?.invoke(t)
//    }
//
//     fun onResponse(call: Call, response: Response) {
//        onResponse?.invoke(response)
//    }
//
//}
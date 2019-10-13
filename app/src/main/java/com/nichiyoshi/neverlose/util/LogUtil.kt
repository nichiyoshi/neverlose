package com.nichiyoshi.neverlose.util

import android.util.Log
import com.nichiyoshi.neverlose.BuildConfig
import java.lang.Exception

class LogUtil {

    companion object{
        fun d(tag: String, message: String?){
            if(!BuildConfig.DEBUG) return
            Log.d(tag, message)
        }

        fun e(tag: String, message: String?){
            if(!BuildConfig.DEBUG) return
            Log.e(tag, message)
        }

        fun e(tag: String, message: String?, exception: Exception?){
            if(!BuildConfig.DEBUG) return
            Log.e(tag, message, exception)
        }

        fun w(tag: String, message: String?){
            if(!BuildConfig.DEBUG) return
            Log.w(tag, message)
        }

        fun w(tag: String, message: String?, exception: Exception?){
            if(!BuildConfig.DEBUG) return
            Log.w(tag, message, exception)
        }
    }

}
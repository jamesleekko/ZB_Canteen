package com.znhst.xtzb.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
        Log.d("save token", token)

        getToken()
    }

    fun getToken(): String? {
        var token = prefs.getString("token", null)
        Log.d("get token", token.toString())
        return token
    }

    fun clearToken() {
        prefs.edit().remove("token").apply()
        Log.d("clear token", "token cleared")
    }
}
package com.znhst.xtzb.network

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

data class ApiErrorBody(
    @Json(name = "status") var status: Int,
    @Json(name = "message") var message: String,
    @Json(name = "timestamp") var timestamp: Long
)

class ErrorInterceptor(private val context: Context,private val logout:() -> Unit) : Interceptor {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val errorAdapter = moshi.adapter(ApiErrorBody::class.java)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        Log.d("response info", response.toString())

        if (!response.isSuccessful) {
            val responseBody = response.peekBody(1024 * 1024)

            val errorResponse = responseBody.string().let { responseString ->
                try {
                    errorAdapter.fromJson(responseString)
                } catch (e: Exception) {
                    Log.e("ErrorInterceptor", "解析结构体失败", e)
                    null
                }
            }

            errorResponse?.let {
                Log.d("response error", "Code: ${it.status}, Message: ${it.message}")
                showApiErrorToast(context, it.message)

                // 处理 401 错误
                if (it.status == 401) {
                    logout()
                }

                throw ApiException(it.status, it.message)
            }
        }

        return response
    }

    private fun showApiErrorToast(context: Context, text: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}

class ApiException(private val statusCode: Int, message: String) : IOException(message) {
    override fun toString(): String {
        return "ApiException(statusCode=$statusCode, message=$message)"
    }
}
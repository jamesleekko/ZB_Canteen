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
    @Json(name = "message") var message: String?,
    //timestamp为Long或者String
    @Json(name = "timestamp") var timestamp: Any?,
)

class ErrorInterceptor(private val context: Context,private val logout:() -> Unit) : Interceptor {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val errorAdapter = moshi.adapter(ApiErrorBody::class.java)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d("请求path:", request.url.encodedPath)
        if (request.url.encodedPath == "/auth/logout") {
            return chain.proceed(request)
        }
        val response = chain.proceed(request)

        Log.d("response info", response.toString())

        if (!response.isSuccessful) {
            val responseBody = response.peekBody(1024 * 1024)

            val errorResponse = responseBody.string().let { responseString ->
                try {
                    errorAdapter.fromJson(responseString)
                } catch (e: Exception) {
                    Log.e("ErrorInterceptor", "接口错误: ", e)
                    logout()
                    null
                }
            }

            errorResponse?.let {
                Log.d("错误详细: ", "Code: ${it.status}, Message: ${it.message}")
                showApiErrorToast(context, it.message?: "未知错误")

                // 处理 401 错误
                if (it.status == 401) {
                    logout()
                }

                throw ApiException(it.status, it.message?: "未知错误")
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
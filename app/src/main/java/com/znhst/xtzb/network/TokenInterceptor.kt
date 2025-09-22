package com.znhst.xtzb.network

import android.content.Context
import com.znhst.xtzb.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val tokenManager: TokenManager, private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenManager.getToken()

        val requestBuilder = originalRequest.newBuilder()

        // 如果 token 不为空，添加到请求头
        token?.let {
            requestBuilder.addHeader("Authorization", it)
//            requestBuilder.addHeader("Authorization", "123321") //debug
        }

        val response = chain.proceed(requestBuilder.build())

        return response
    }
}
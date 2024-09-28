package com.znhst.xtzb.network

import android.content.Context
import com.znhst.xtzb.utils.TokenManager
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

data class LoginUser(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String,
    @Json(name = "code") val code: String,
    @Json(name = "uuid") val uuid: String,
)

data class RegisterUser(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "email") val email: String? = null,
)

data class AuthResponse(@Json(name = "token") val token: String)

data class CaptchaResponse(
    @Json(name = "img") val img: String,
    @Json(name = "uuid") val uuid: String
)

interface ApiService {

    @POST("/api/users/register")
    @Headers("Content-Type: application/json")
    suspend fun register(@Body user: RegisterUser): Response<Void>

    @POST("/auth/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body user: LoginUser): AuthResponse

    @DELETE("/auth/logout")
    @Headers("Content-Type: application/json")
    suspend fun logout()

    @GET("/auth/code")
    @Headers("Content-Type: application/json")
    suspend fun getCaptcha(): CaptchaResponse
}

object ApiClient {
    private lateinit var tokenManager: TokenManager
    private lateinit var retrofit: Retrofit

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun init(tokenManager: TokenManager, context: Context, logout: () -> Unit) {
        this.tokenManager = tokenManager

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(tokenManager, context))
            .addInterceptor(ErrorInterceptor(context, logout))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000") // 替换成你的后端 URL
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
package com.znhst.xtzb.network

import android.content.Context
import com.znhst.xtzb.utils.TokenManager
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.znhst.xtzb.dataModel.ZBDeviceCategory
import com.znhst.xtzb.dataModel.ZBDeviceInfo
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

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

data class AACStatusParams(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "deviceSerial") val deviceSerial: String,
    @Json(name = "localIndex") val localIndex: String?,
)

data class EZTokenResponse(
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "expireTime") val expireTime: Long
)

data class EZTokenOuterResponse(
    @Json(name = "data") val data: EZTokenResponse?,
    @Json(name = "code") val code: String,
    @Json(name = "msg") val msg: String
)

data class AuthResponse(@Json(name = "token") val token: String)

data class CaptchaResponse(
    @Json(name = "img") val img: String,
    @Json(name = "uuid") val uuid: String
)

data class AACTransferStatusData(
    @Json(name = "enable") val enable: Boolean
)

data class AACTransferStatusMeta(
    @Json(name = "code") val code: Int,
    @Json(name = "message") val message: String,
    @Json(name = "moreInfo") val moreInfo: Any?
)

data class AACTransferStatusResponse(
    @Json(name = "data") val data: AACTransferStatusData,
    @Json(name = "meta") val meta: AACTransferStatusMeta,
)

data class AACSettingStatusResponse(
    @Json(name = "meta") val meta: AACTransferStatusMeta,
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

    @POST("/api/lapp/token/get")
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getEZToken(
        @Field("appKey") appKey: String,
        @Field("appSecret") appSecret: String
    ): EZTokenOuterResponse

    @GET("/api/service/media/aac/transfer")
    @Headers("Content-Type: application/json")
    suspend fun getAACTransferStatus(
        @Header("accessToken") accessToken: String,
        @Header("deviceSerial") deviceSerial: String,
        @Header("localIndex") localIndex: String?
    ): AACTransferStatusResponse

    @POST("/api/service/media/aac/transfer")
    @Headers("Content-Type: application/json")
    suspend fun setAACTransferStatus(
        @Header("accessToken") accessToken: String,
        @Header("deviceSerial") deviceSerial: String,
        @Header("localIndex") localIndex: String?,
        @Query("enable") enable: Int
    ): AACSettingStatusResponse

    @GET("/zb/device_category_list")
    @Headers("Content-Type: application/json")
    suspend fun getDeviceCategories(): List<ZBDeviceCategory>

    @GET("/zb/camera_list")
    @Headers("Content-Type: application/json")
    suspend fun getCameras(): List<ZBDeviceInfo>
}

object ApiClient {
    private lateinit var tokenManager: TokenManager
    private lateinit var retrofit: Retrofit
    private lateinit var ezRetrofit: Retrofit

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun init(tokenManager: TokenManager, context: Context, logout: () -> Unit) {
        this.tokenManager = tokenManager

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(tokenManager, context))
            .addInterceptor(ErrorInterceptor(context, logout))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
//            .baseUrl("http://192.168.1.6:8000")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

        ezRetrofit = Retrofit.Builder()
            .baseUrl("https://open.ys7.com")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    val ezApiService: ApiService by lazy {
        ezRetrofit.create(ApiService::class.java)
    }
}
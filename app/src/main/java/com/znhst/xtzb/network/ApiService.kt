package com.znhst.xtzb.network

import android.content.Context
import com.znhst.xtzb.utils.TokenManager
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.znhst.xtzb.BuildConfig
import com.znhst.xtzb.dataModel.StockCategory
import com.znhst.xtzb.dataModel.DoorInfo
import com.znhst.xtzb.dataModel.EZDeviceCategory
import com.znhst.xtzb.dataModel.EZDeviceInfo
import com.znhst.xtzb.dataModel.EZNewsCategory
import com.znhst.xtzb.dataModel.FreezerInfo
import com.znhst.xtzb.dataModel.SmokeAlarmInfo
import com.znhst.xtzb.dataModel.Stock
import com.znhst.xtzb.dataModel.StockInboundRecord
import com.znhst.xtzb.dataModel.StockItem
import com.znhst.xtzb.dataModel.StockOutboundRecord
import com.znhst.xtzb.dataModel.TempHumiInfo
import com.znhst.xtzb.dataModel.TempHumiCategory
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

data class UserInfoResponse(
    @Json(name = "user") val user: UserInfo
)

data class Dept(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String
)

data class UserInfo(
    @Json(name = "username") val userName: String,
    @Json(name = "nickName") val nickName: String,
    @Json(name = "avatarName") val avatarName: String?,
    @Json(name = "dept") val dept: Dept,
    @Json(name = "phone") val phone: String,
)

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

data class NewsType(
    @Json(name = "type") val type: Int,
    @Json(name = "display_name") val displayName: String
)

data class NewsListResponse(
    @Json(name = "list") val list: List<NewsItem>,
    @Json(name = "total") val total: Int,
)

data class NewsItem(
    @Json(name = "id") val id: Long,
    @Json(name = "title") val title: String,
    @Json(name = "typeName") val typeName: String?,
    @Json(name = "kindName") val kindName: String,
    @Json(name = "kindDisplayName") val kindDisplayName: String,
    @Json(name = "fileName") val fileName: String?,
    @Json(name = "filePath") val filePath: String?,
    @Json(name = "content") val content: String?,
    @Json(name = "deptName") val deptName: String?,
    @Json(name = "updateTime") val updateTime: String,
)

data class NewsListRequest(
    @Json(name = "type") val type: Int?,      // 新闻类型
    @Json(name = "page") val page: Int?,      // 页码
    @Json(name = "pageSize") val pageSize: Int?     // 每页数量
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

data class DayufengResponse<T>(
    @Json(name = "data") val data: T?,
    @Json(name = "code") val code: Int,
    @Json(name = "msg") val msg: String
)

data class DayufengData<T>(
    @Json(name = "data") val data: T
)

data class DayufengHistoryData<T>(
    @Json(name = "th") val th: List<ThProperty>,
    @Json(name = "data") val data: List<T>
)

data class ThProperty(
    @Json(name = "prop") val prop: String,
    @Json(name = "label") val label: String,
    @Json(name = "beishu") val beishu: Int,
    @Json(name = "t") val t: Int,
    @Json(name = "n") val n: Int,
    @Json(name = "danwei") val danwei: String
)

data class FreezerEntry(
    @Json(name = "shebeibianhao") val deviceNo: String,
    @Json(name = "time") val time: String,
    @Json(name = "open1") val doorStatus: String,
    @Json(name = "xinhao") val signalStrength: String,
    @Json(name = "address") val address: String,
    @Json(name = "power") val power: String,
    @Json(name = "jingdu") val longitude: String,
    @Json(name = "weidu") val latitude: String,
    @Json(name = "coordinate_type") val coordinateType: Int,
    @Json(name = "id") val id: Int
)

data class SmokeAlarmEntry(
    @Json(name = "shebeibianhao") val deviceNo: String,
    @Json(name = "time") val time: String,
    @Json(name = "open1") val doorStatus: String,
    @Json(name = "xinhao") val signalStrength: String,
    @Json(name = "address") val address: String,
    @Json(name = "power") val power: String,
    @Json(name = "jingdu") val longitude: String,
    @Json(name = "weidu") val latitude: String,
    @Json(name = "coordinate_type") val coordinateType: Int,
    @Json(name = "id") val id: Int
)

data class DoorOperationEntry(
    @Json(name = "log_sn") val logSn: String,
    @Json(name = "door_guid") val doorGuid: String,
    @Json(name = "door_name") val doorName: String,
    @Json(name = "member_xm") val memberXm: String,
    @Json(name = "visit_time") val visitTime: String,
    @Json(name = "img_url") val imgUrl: String?,
    @Json(name = "open_way") val openWay: Int,
    // 开门方式 1:二维码 2:刷卡 3:人脸 4:密码' 5：扫码开门 9：远程开门
    @Json(name = "open_status") val openStatus: Int,
    // 开门状态 1:成功 2:失败
    @Json(name = "remark") val remark: String,
)

data class TempHumiEntry(
    @Json(name = "shebeibianhao") val deviceNo: String,
    @Json(name = "time") val time : String,
    @Json(name = "temperature01") val temp: String,
    @Json(name = "humidity") val humi: String,
    @Json(name = "xinhao") val signal: String,
    @Json(name = "jingdu") val longitude: String,
    @Json(name = "weidu") val latitude: String,
    @Json(name = "coordinate_type") val coordinateType: Int,
    @Json(name = "power") val power: String,
    @Json(name = "id") val id: Int
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

    @GET("/auth/info")
    @Headers("Content-Type: application/json")
    suspend fun getUserInfo(): UserInfoResponse

    @POST("/api/lapp/token/get")
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun getEZToken(
        @Field("appKey") appKey: String,
        @Field("appSecret") appSecret: String
    ): EZTokenOuterResponse

    @GET("/dayufeng/token")
    @Headers("Content-Type: application/json")
    suspend fun getDayufengToken(): String

    @GET("/zb/device_category_list")
    @Headers("Content-Type: application/json")
    suspend fun getDeviceCategories(): List<EZDeviceCategory>

    @GET("/zb/camera_list")
    @Headers("Content-Type: application/json")
    suspend fun getCameras(): List<EZDeviceInfo>

    @GET("/dayufeng/freezer_list")
    @Headers("Content-Type: application/json")
    suspend fun getDayufengFreezers(): List<FreezerInfo>

    @GET("/dayufeng/smoke_alarm_list")
    @Headers("Content-Type: application/json")
    suspend fun getDayufengSmokeAlarms(): List<SmokeAlarmInfo>

    @GET("/dayufeng/door_list")
    @Headers("Content-Type: application/json")
    suspend fun getDayufengDoors(): List<DoorInfo>

    @GET("/dayufeng/temp_humi_list")
    @Headers("Content-Type: application/json")
    suspend fun getTempHumis(@Query("category") category: TempHumiCategory): List<TempHumiInfo>

    @GET("/devices_data_v2")
    @Headers("Content-Type: application/json")
    suspend fun getFreezerHistory(
        @Header("Authorization") token: String,
        @Query("login_type") loginType: String,
        @Query("shebeibianhao") deviceNo: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("startTime", encoded = true) startTime: String,
        @Query("endTime", encoded = true) endTime: String
    ): DayufengResponse<DayufengData<DayufengHistoryData<FreezerEntry>>>

    @GET("/devices_data_v2")
    @Headers("Content-Type: application/json")
    suspend fun getSmokeAlarmHistory(
        @Header("Authorization") token: String,
        @Query("login_type") loginType: String,
        @Query("shebeibianhao") deviceNo: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("startTime", encoded = true) startTime: String,
        @Query("endTime", encoded = true) endTime: String
    ): DayufengResponse<DayufengData<DayufengHistoryData<SmokeAlarmEntry>>>

    @GET("/devices_data_v2")
    @Headers("Content-Type: application/json")
    suspend fun getTempHumiHistory(
        @Header("Authorization") token: String,
        @Query("login_type") loginType: String,
        @Query("shebeibianhao") deviceNo: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("startTime", encoded = true) startTime: String,
        @Query("endTime", encoded = true) endTime: String,
    ): DayufengResponse<DayufengData<DayufengHistoryData<TempHumiEntry>>>

    @GET("/door_log_list")
    @Headers("Content-Type: application/json")
    suspend fun getDoorHistory(
        @Header("Authorization") token: String,
        @Query("login_type") loginType: Int,
        @Query("door_guid") doorGuid: String?,
        @Query("page") page: Int?,
        @Query("per_page") limit: Int?,
        @Query("start_time", encoded = true) startTime: String?,
        @Query("end_time", encoded = true) endTime: String?
    ): DayufengResponse<DayufengData<List<DoorOperationEntry>>>

    @GET("/news/category_list")
    @Headers("Content-Type: application/json")
    suspend fun getNewsCategories(): List<EZNewsCategory>

    @POST("/news/news_list")
    @Headers("Content-Type: application/json")
    suspend fun getNewsList(@Body request: NewsListRequest): NewsListResponse

    @GET("/stock/items")
    @Headers("Content-Type: application/json")
    suspend fun getStockItems(): List<StockItem>

    @GET("/stock/stocks")
    @Headers("Content-Type: application/json")
    suspend fun getStocks(): List<Stock>

    @POST("/stock/inbound")
    @Headers("Content-Type: application/json")
    suspend fun addInboundRecord(@Body inboundRecord: StockInboundRecord): Response<Void>

    @POST("/stock/outbound")
    @Headers("Content-Type: application/json")
    suspend fun addOutboundRecord(@Body outboundRecord: StockOutboundRecord): Response<Void>
}

object ApiClient {
    private lateinit var tokenManager: TokenManager
    private lateinit var retrofit: Retrofit
    private lateinit var ezRetrofit: Retrofit
    private lateinit var dayufengRetrofit: Retrofit

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun init(tokenManager: TokenManager, context: Context, logout: () -> Unit) {
        this.tokenManager = tokenManager

        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(tokenManager, context))
            .addInterceptor(ErrorInterceptor(context, logout))
            .build()

        val dayufengClient = OkHttpClient.Builder()
            .addInterceptor(ErrorInterceptor(context, logout))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

        ezRetrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.EZ_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()

        dayufengRetrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.DYF_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(dayufengClient)
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val ezApiService: ApiService by lazy {
        ezRetrofit.create(ApiService::class.java)
    }

    val dayufengApiService: ApiService by lazy {
        dayufengRetrofit.create(ApiService::class.java)
    }
}
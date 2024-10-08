package com.znhst.xtzb.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.network.ApiException
import kotlinx.coroutines.delay
import retrofit2.HttpException

class EZViewModel(application: Application) : AndroidViewModel(application) {
    var accessToken = mutableStateOf("")
    var expireTime = mutableStateOf(0L)

    suspend fun getEZToken(appKey: String, appSecret: String): Result<String> {
        Log.d("accesstoken res123", "$appKey $appSecret")
//        delay(2000)
        return try {
            val response = ApiClient.ezApiService.getEZToken(appKey, appSecret)
            response.data?.let { Log.d("accesstoken res123456", it.accessToken) }
            accessToken.value = response.data?.accessToken ?: ""
            expireTime.value = response.data?.expireTime ?: 283719837291L
            Result.success("萤石token获取成功")
        } catch (e: HttpException) {
            Result.failure(Exception("获取萤石token失败: ${e.message()}"))
        } catch (e: ApiException) {
            Result.failure(Exception("获取萤石token失败: ${e.message}"))
        }
    }
}
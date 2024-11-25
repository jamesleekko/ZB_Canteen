package com.znhst.xtzb.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.network.ApiException
import com.znhst.xtzb.state.GlobalData
import retrofit2.HttpException

class EZViewModel(application: Application) : AndroidViewModel(application) {
    val accessToken: LiveData<String> = GlobalData.ezAccessToken
    var expireTime :LiveData<Long> = GlobalData.ezExpireTime

    suspend fun getEZToken(appKey: String, appSecret: String): Result<String> {
        Log.d("accesstoken res", "$appKey $appSecret")
        return try {
            val response = ApiClient.ezApiService.getEZToken(appKey, appSecret)
            response.data?.let { Log.d("accesstoken res123456", it.accessToken) }
            GlobalData.updateEZAccessToken(response.data?.accessToken ?: "")
            GlobalData.updateEZExpireTime(response.data?.expireTime ?: 283719837291L)
            Result.success("萤石token获取成功")
        } catch (e: HttpException) {
            Result.failure(Exception("获取萤石token失败: ${e.message()}"))
        } catch (e: ApiException) {
            Result.failure(Exception("获取萤石token失败: ${e.message}"))
        }
    }
}
package com.znhst.xtzb.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.network.Dept
import com.znhst.xtzb.network.UserInfo
import com.znhst.xtzb.utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    var tokenManager = TokenManager(getApplication<Application>().applicationContext)
    var userInfo = mutableStateOf(
        UserInfo(
            userName = "",
            nickName = "",
            avatarName = "",
            dept = Dept(0L, ""),
            phone = ""
        )
    )

    fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val result = ApiClient.apiService.getUserInfo()
                Log.d("获取用户信息:", result.toString())
                userInfo.value = UserInfo(
                    userName = result.user.userName,
                    nickName = result.user.nickName,
                    avatarName = result.user.avatarName,
                    dept = result.user.dept,
                    phone = result.user.phone
                )
                Result.success(true)
            } catch (e: HttpException) {
                Result.failure(Exception("获取用户信息失败: ${e.message()}"))
            }
        }
    }
}
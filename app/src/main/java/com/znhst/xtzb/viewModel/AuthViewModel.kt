package com.znhst.xtzb.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.network.ApiException
import com.znhst.xtzb.network.LoginUser
import com.znhst.xtzb.network.RegisterUser
import com.znhst.xtzb.utils.TokenManager
import com.znhst.xtzb.utils.getPublicKey
import com.znhst.xtzb.utils.publickey
import com.znhst.xtzb.utils.rsaEncrypt
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class CaptchaResponse(var img: String, var uuid: String)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    var captchaState = mutableStateOf(CaptchaResponse("", ""))
    var tokenManager = TokenManager(getApplication<Application>().applicationContext)

    suspend fun register(
        username: String,
        password: String,
        phone: String,
        email: String? = null
    ): Result<String> {
        val encryptedPassword = rsaEncrypt(password, getPublicKey(publickey))
        return try {
            ApiClient.apiService.register(
                RegisterUser(
                    username,
                    encryptedPassword,
                    phone,
                    email
                )
            )
            Result.success("注册成功，请登录")
        } catch (e: HttpException) {
            Result.failure(Exception("注册失败: ${e.message()}"))
        } catch( e: ApiException) {
            Result.failure(Exception("注册失败: ${e.message}"))
        }
    }

    suspend fun login(
        username: String,
        password: String,
        code: String,
        uuid: String = captchaState.value.uuid
    ) :Result<String> {
        val encryptedPassword = rsaEncrypt(password, getPublicKey(publickey))
        return try {
            val response =
                ApiClient.apiService.login(LoginUser(username, encryptedPassword, code, uuid))
            tokenManager.saveToken(response.token)
            Log.d("login res", response.toString())
            Result.success("登录成功")
        } catch (e: HttpException) {
            Result.failure(Exception("登录失败: ${e.message()}"))
        } catch( e: ApiException) {
            Result.failure(Exception("登录失败: ${e.message}"))
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                ApiClient.apiService.logout()
                tokenManager.clearToken()
                onSuccess()
                Log.d("注销成功","123")
            } catch (e: HttpException) {
                Log.d("注销失败",e.message())
            }
        }
    }

    suspend fun getCaptcha(): Result<Boolean> {
        return try {
            val response = ApiClient.apiService.getCaptcha()
            captchaState.value = (CaptchaResponse(response.img, response.uuid))
            Result.success(true)
        } catch (e: HttpException) {
            Result.failure(Exception("获取验证码失败: ${e.message()}"))
        }
    }
}
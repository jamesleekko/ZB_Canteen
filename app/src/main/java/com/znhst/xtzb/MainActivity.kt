package com.znhst.xtzb

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.znhst.xtzb.ui.page.AuthScreen
import com.znhst.xtzb.ui.theme.ZB_CanteenTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.videogo.openapi.EZOpenSDK
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.ui.page.CameraList
import com.znhst.xtzb.ui.page.MainPage
import com.znhst.xtzb.ui.page.RegisterScreen
import com.znhst.xtzb.utils.TokenManager
import com.znhst.xtzb.viewModel.AuthViewModel
import com.znhst.xtzb.viewModel.EZViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenManager = TokenManager(this)

        val appKey = "1c66441dc45f4e939689a2ba4b901bcd"
        val secretKey = "146aa934f75321813042020a9237ee86"

        val ezViewModel = EZViewModel(application)

        setContent {
            ZB_CanteenTheme {
                MyMain(this, tokenManager)
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                ezViewModel.getEZToken(appKey, secretKey)

                // 开启 SDK 日志（正式发布时去掉）
                EZOpenSDK.showSDKLog(true)
                // 设置是否支持P2P取流
                EZOpenSDK.enableP2P(true)
                // 初始化 SDK，替换成你申请的 APP_KEY
                EZOpenSDK.initLib(application, appKey)
                val instance = EZOpenSDK.getInstance()
                ezViewModel.accessToken.value.let {
                    if (it != "") {
                        Log.d("设置token", it.toString())
                        instance.setAccessToken((it))
                    }
                }
            } catch (e: Exception) {
                Log.e("EZ Init Error", "Failed to init: ${e.message}")
            }
        }
    }
}

@Composable
fun MyMain(context: Context, tokenManager: TokenManager, viewModel: AuthViewModel = viewModel()) {
    val navController = rememberNavController()

    val hasToken = remember { TokenManager(context).getToken() != null }

    val logout: () -> Unit = {
        viewModel.logout {
            navController.navigate("login") {
                popUpTo("main") { inclusive = true } // 清空返回栈
            }
        }
    }

    ApiClient.init(tokenManager, context, logout)

    // 定义导航结构
    NavHost(navController = navController, startDestination = if (hasToken) "main" else "login") {
        composable("login") { AuthScreen(context, viewModel(), navController = navController) }
        composable("register") { RegisterScreen(viewModel(), navController = navController) }
        composable("main") { MainPage(navController = navController) }
    }
}
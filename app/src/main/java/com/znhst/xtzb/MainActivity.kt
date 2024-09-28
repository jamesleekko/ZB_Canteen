package com.znhst.xtzb

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.znhst.xtzb.ui.page.MainPage
import com.znhst.xtzb.ui.page.RegisterScreen
import com.znhst.xtzb.utils.TokenManager
import com.znhst.xtzb.viewModel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tokenManager = TokenManager(this)

        val appKey = "1c66441dc45f4e939689a2ba4b901bcd"
        val defaultToken = "at.1lszaid9bojnn1xk4h8k7mw014w9q6cv-6f0nozgfch-1ug0drv-4cnlkih65"

        setContent {
            ZB_CanteenTheme {
                MyMain(this, tokenManager)
            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            // 开启 SDK 日志（正式发布时去掉）
            EZOpenSDK.showSDKLog(true)
            // 设置是否支持P2P取流
            EZOpenSDK.enableP2P(true)
            // 初始化 SDK，替换成你申请的 APP_KEY
            EZOpenSDK.initLib(application, appKey)
            val instance = EZOpenSDK.getInstance()
            instance.setAccessToken((defaultToken))

//            try {
//                val deviceList = withContext(Dispatchers.IO) {
//                    instance.getDeviceList(0, 10)
//                }
//                val myCamera = withContext(Dispatchers.IO) {
//                    instance.getDeviceInfo("BC5617792")
//                }
//                Toast.makeText(
//                    applicationContext,
//                    if (deviceList == null) "没有列表" else deviceList.toString(),
//                    Toast.LENGTH_SHORT
//                ).show()
//            } catch (e: Exception) {
//                // 捕获并处理异常
//                Log.e("Error Device List", "Failed to fetch device list: ${e.message}")
//            }
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
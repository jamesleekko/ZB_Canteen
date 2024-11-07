package com.znhst.xtzb.ui.page

import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@Composable
fun VR(navController: NavController) {
    val context = LocalContext.current
//    val vrUrl = "https://www.baidu.com"
    val vrUrl = "https://web.xiaohongwu.com/hotspot/preview?p=s-42f8a7bbb16b414c84eb931665906c17"

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = {
            WebView(context).apply {
                settings.javaScriptEnabled = true

                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

                // 设置自定义 User-Agent
                settings.userAgentString =
                    "Mozilla/5.0 (Linux; Android 10; Mobile; rv:89.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Mobile Safari/537.36"

                webViewClient = object : WebViewClient() {
                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: SslErrorHandler?,
                        error: SslError?
                    ) {
                        handler?.proceed() // 忽略 SSL 错误，仅限测试用
                    }
                }

                loadUrl(vrUrl)
            }
        }, modifier = Modifier.fillMaxSize())

        // 添加一个左上角的退出按钮，返回到上一级
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(36.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "退出 VR 页面",
                tint = Color.White,
            )
        }
    }
}
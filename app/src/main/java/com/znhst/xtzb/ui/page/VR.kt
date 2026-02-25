package com.znhst.xtzb.ui.page

import android.content.Context
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebStorage

@Composable
fun VR(navController: NavController) {
    val context = LocalContext.current
    val vrUrl = "https://web.xiaohongwu.com/hotspot/preview?p=s-42f8a7bbb16b414c84eb931665906c17"

//    LaunchedEffect(Unit) {
//        val map = hashMapOf<String, Any>(
//            TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
//            TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true
//        )
//        QbSdk.initTbsSettings(map)
//    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = {
            WebView(context).apply {
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.setSupportZoom(true)
                settings.builtInZoomControls = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        view?.loadUrl(url ?: "")
                        return true
                    }
                }

                loadUrl(vrUrl)
            }
        }, modifier = Modifier.fillMaxSize())

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(36.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "退出 VR 页面",
                tint = Color.White,
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            clearWebViewData(context)
        }
    }
}


private fun clearWebViewData(context: Context) {
    WebView(context).apply {
        clearCache(true)
        clearHistory()
    }

    // Clear cookies and local storage
    CookieManager.getInstance().removeAllCookies(null)
    CookieManager.getInstance().flush()
    WebStorage.getInstance().deleteAllData()
}
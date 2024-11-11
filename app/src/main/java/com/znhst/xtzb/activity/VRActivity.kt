package com.znhst.xtzb.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebStorage
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient


class VRActivity : AppCompatActivity() {

    private lateinit var x5WebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val map = hashMapOf<String, Any>(
            TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
            TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true
        )
        QbSdk.initTbsSettings(map)

        x5WebView = WebView(this)
        setContentView(x5WebView)

        setupX5WebView()
        x5WebView.loadUrl("https://web.xiaohongwu.com/hotspot/preview?p=s-42f8a7bbb16b414c84eb931665906c17")
    }

    private fun setupX5WebView() {
        x5WebView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.LOAD_NORMAL
            setAppCacheEnabled(true)
            setSupportZoom(true)
            builtInZoomControls = true
            allowFileAccess = true
            allowContentAccess = true
        }

        x5WebView.webViewClient = object : WebViewClient() {
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                // Ignore SSL errors (use with caution in production)
                handler?.proceed()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearWebViewData()
        x5WebView.destroy()
    }

    private fun clearWebViewData() {
        x5WebView.clearCache(true)
        x5WebView.clearHistory()
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }
}
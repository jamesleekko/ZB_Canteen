package com.znhst.xtzb

import android.app.Application
import android.util.Log
import com.videogo.openapi.EZOpenSDK

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("321321321","321321321")

        val appKey = "1c66441dc45f4e939689a2ba4b901bcd"
        val defaultToken = "at.dfsvk7oq54tjjzhcbxbf2juu84qzf5o8-1vymg7lcoc-0m0k1eu-bjpyvzpuo"

        // 开启 SDK 日志（正式发布时去掉）
        EZOpenSDK.showSDKLog(true)

        // 设置是否支持P2P取流
        EZOpenSDK.enableP2P(false)

        // 初始化 SDK，替换成你申请的 APP_KEY
        EZOpenSDK.initLib(this, appKey)

        EZOpenSDK.getInstance().setAccessToken((defaultToken))

        val cameraList = EZOpenSDK.getInstance().getDeviceList(0,10)
        Log.d("cameras", cameraList.toString())
    }
}
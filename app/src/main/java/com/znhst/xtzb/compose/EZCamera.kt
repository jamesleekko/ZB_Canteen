package com.znhst.xtzb.compose

import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.videogo.openapi.EZOpenSDK

@Composable
fun EZCameraView() {
    val MSG_ON_DEVICE_RESPONSE =  1

    var player = EZOpenSDK.getInstance().createPlayer("BC5617792", 1);
    val handler = Handler(Looper.getMainLooper()) {msg ->
        when (msg.what) {
            MSG_ON_DEVICE_RESPONSE -> {

            }
        }
        true
    }
    player.setHandler(handler)

    fun startPlay() {
//        player.setSurfaceHold()
    }

    AndroidView(
        factory = { context ->
            SurfaceView(context).apply {
                // 配置 SurfaceView
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        // 处理 SurfaceView 创建后的一些初始化操作

                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        // 处理 SurfaceView 大小变化
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        // 清理资源
                    }
                })
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
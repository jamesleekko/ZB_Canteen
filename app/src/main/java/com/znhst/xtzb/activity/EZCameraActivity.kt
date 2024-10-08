package com.znhst.xtzb.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.EZPlayer

class EZCameraActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val cameraSerial = intent.getStringExtra("cameraSerial")
        val cameraNo = intent.getIntExtra("cameraNo", 1)

        setContent {
            if (cameraSerial != null) {
                CameraScreen(cameraSerial, cameraNo)
            } else {
                finish()
            }
        }
    }
}

@Composable
fun CameraScreen(cameraSerial: String, cameraNo: Int) {
    var player: EZPlayer? = null

    fun startCameraPreview(holder: SurfaceHolder) {
            player = EZOpenSDK.getInstance().createPlayer(cameraSerial, cameraNo)
            player?.setSurfaceHold(holder)
            player?.startRealPlay() // 开始播放
    }

    fun stopCameraPreview() {
        // 停止播放并释放播放器资源
        EZOpenSDK.getInstance().releasePlayer(player)
    }

    AndroidView(
        factory = { context ->
            SurfaceView(context).apply {
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) {
                        // 当 SurfaceView 创建时，初始化播放
                        startCameraPreview(holder)
                    }

                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        // Surface 大小变化时处理
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) {
                        // 销毁时释放资源
                        stopCameraPreview()
                    }
                })
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
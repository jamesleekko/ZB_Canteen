package com.znhst.xtzb.activity

import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.videogo.openapi.EZConstants
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.EZPlayer
import com.znhst.xtzb.viewModel.EZViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EZCameraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    val top: EZConstants.EZPTZCommand = EZConstants.EZPTZCommand.EZPTZCommandUp
    val down: EZConstants.EZPTZCommand = EZConstants.EZPTZCommand.EZPTZCommandDown
    val left: EZConstants.EZPTZCommand = EZConstants.EZPTZCommand.EZPTZCommandLeft
    val right: EZConstants.EZPTZCommand = EZConstants.EZPTZCommand.EZPTZCommandRight
    val start: EZConstants.EZPTZAction = EZConstants.EZPTZAction.EZPTZActionSTART
    val stop: EZConstants.EZPTZAction = EZConstants.EZPTZAction.EZPTZActionSTOP
    val speed: Int = EZConstants.PTZ_SPEED_DEFAULT

    val scope = rememberCoroutineScope()

    fun startCameraMovement(direction: EZConstants.EZPTZCommand) {
        scope.launch(Dispatchers.IO) {
            try {
                EZOpenSDK.getInstance().controlPTZ(cameraSerial, cameraNo, direction, start, speed)
                delay(500)
                EZOpenSDK.getInstance().controlPTZ(cameraSerial, cameraNo, direction, stop, speed)
            } catch (e: Exception) {
                Log.e("camera movement error", e.toString())
            }
        }
    }

    fun startCameraPreview(holder: SurfaceHolder) {
        player = EZOpenSDK.getInstance().createPlayer(cameraSerial, cameraNo)
        player?.setSurfaceHold(holder)
        player?.startRealPlay() // 开始播放
    }

    fun stopCameraPreview() {
        // 停止播放并释放播放器资源
        EZOpenSDK.getInstance().releasePlayer(player)
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        val parentWidth = constraints.maxWidth.toFloat()
        val parentHeight = constraints.maxHeight.toFloat()
        Log.d("constraints size", "width: $parentWidth, height: $parentHeight")

        AndroidView(
            factory = { context ->
                SurfaceView(context).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            // 当 SurfaceView 创建时，初始化播放
                            startCameraPreview(holder)
                        }

                        override fun surfaceChanged(
                            holder: SurfaceHolder, format: Int, width: Int, height: Int
                        ) {

                        }

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            // 销毁时释放资源
                            stopCameraPreview()
                        }
                    })
                }
            }, modifier = Modifier.fillMaxSize()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 220.dp)
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {
                    startCameraMovement(left)
                }, modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(4.dp)
            ) {
                Text(text = "←")
            }
            Button(
                onClick = {
                    startCameraMovement(right)
                }, modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(4.dp)
            ) {
                Text(text = "→")
            }
            Button(
                onClick = {
                    startCameraMovement(top)
                }, modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(4.dp)
            ) {
                Text(text = "↑")
            }
            Button(
                onClick = {
                    startCameraMovement(down)
                }, modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(4.dp)
            ) {
                Text(text = "↓")
            }
        }
    }
}
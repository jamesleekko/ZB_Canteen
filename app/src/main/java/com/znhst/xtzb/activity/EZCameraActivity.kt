package com.znhst.xtzb.activity

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.videogo.openapi.EZConstants
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.EZPlayer
import com.znhst.xtzb.compose.CircularRemoteControl
import com.znhst.xtzb.viewModel.EZViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EZCameraActivity : ComponentActivity() {
    private fun requestAudioPermission() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        }

//        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // 处理用户拒绝权限请求的情况
            Toast.makeText(this, "音频权限被拒绝", Toast.LENGTH_SHORT).show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAudioPermission()

        val cameraSerial = intent.getStringExtra("cameraSerial")
        val cameraNo = intent.getIntExtra("cameraNo", 1)

        setContent {
            if (cameraSerial != null) {
                CameraScreen(cameraSerial, cameraNo, application)
            } else {
                finish()
            }
        }
    }
}

@Composable
fun CameraScreen(cameraSerial: String, cameraNo: Int, application: Application) {
    var player: EZPlayer? = null

    val top: EZConstants.EZPTZCommand = EZConstants.EZPTZCommand.EZPTZCommandUp
    val down: EZConstants.EZPTZCommand = EZConstants.EZPTZCommand.EZPTZCommandDown
    val left: EZConstants.EZPTZCommand = EZConstants.EZPTZCommand.EZPTZCommandLeft
    val right: EZConstants.EZPTZCommand = EZConstants.EZPTZCommand.EZPTZCommandRight
    val start: EZConstants.EZPTZAction = EZConstants.EZPTZAction.EZPTZActionSTART
    val stop: EZConstants.EZPTZAction = EZConstants.EZPTZAction.EZPTZActionSTOP
    val speed: Int = EZConstants.PTZ_SPEED_DEFAULT

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val ezViewModel = EZViewModel(application)
    val token by ezViewModel.accessToken.observeAsState("")

    fun startCameraMovement(direction: EZConstants.EZPTZCommand) {
        Log.d("摄像头移动", "move")
        scope.launch(Dispatchers.IO) {
            try {
                EZOpenSDK.getInstance().controlPTZ(cameraSerial, cameraNo, direction, start, speed)
            } catch (e: Exception) {
                Log.e("camera movement error", e.toString())
            }
        }
    }

    fun stopCameraMovement(direction: EZConstants.EZPTZCommand) {
        Log.d("摄像头移动停止", "stop")
        scope.launch(Dispatchers.IO) {
            try {
                EZOpenSDK.getInstance().controlPTZ(cameraSerial, cameraNo, direction, stop, speed)
            } catch (e: Exception) {
                Log.e("camera stop error", e.toString())
            }
        }
    }

    fun startCameraPreview(holder: SurfaceHolder) {
        scope.launch {
            player = EZOpenSDK.getInstance().createPlayer(cameraSerial, cameraNo)
            player?.setSurfaceHold(holder)
            player?.startRealPlay() // 开始播放
            delay(2000)
            player?.openSound()
//            delay(2000)
//            player?.startVoiceTalk()
//            delay(4000)
//            player?.stopVoiceTalk()
        }
    }

    fun stopCameraPreview() {
        // 停止播放并释放播放器资源
        EZOpenSDK.getInstance().releasePlayer(player)
    }

    fun addImageToGallery(filePath: String) {
        MediaScannerConnection.scanFile(context, arrayOf(filePath), null, null)
    }

    fun saveBitmap(bitmap: Bitmap) {
        Log.d("SaveBitmap", bitmap.toString())
        val filename = "众搏摄像头截图_${cameraSerial}_${System.currentTimeMillis()}.png"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename)

        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            addImageToGallery(file.absolutePath)
            Log.d("SaveBitmap", "Image saved: ${file.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("SaveBitmap", "Error saving image: ${e.message}")
        }
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
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        CircularRemoteControl(
            onUpDown = { startCameraMovement(top) },
            onDownDown = { startCameraMovement(down) },
            onLeftDown = { startCameraMovement(left) },
            onRightDown = { startCameraMovement(right) },
            onUpUp = { stopCameraMovement(top) },
            onDownUp = { stopCameraMovement(down) },
            onLeftUp = { stopCameraMovement(left) },
            onRightUp = { stopCameraMovement(right) }
        )

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = {

            }) {
                Text("录屏")
            }
            Button(onClick = {
                player?.capturePicture().let {
                    if (it != null) {
                        saveBitmap(it)
                    }
                }
            }) {
                Text("截图")
            }
        }
    }
}
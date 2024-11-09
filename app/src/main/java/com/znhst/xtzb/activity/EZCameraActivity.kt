package com.znhst.xtzb.activity

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
import com.videogo.openapi.EZConstants
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.EZPlayer
import com.videogo.util.LocalInfo
import com.znhst.xtzb.compose.CircularRemoteControl
import com.znhst.xtzb.utils.addVideoToGalleryFromPrivateDir
import com.znhst.xtzb.utils.saveBitmapToMediaStore
import com.znhst.xtzb.utils.saveBitmapToPublicDir
import com.znhst.xtzb.viewModel.EZViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class EZCameraActivity : ComponentActivity() {
    private fun requestAudioPermission() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 100
            )
        }
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
                CameraScreen(cameraSerial, cameraNo, application, this)
            } else {
                finish()
            }
        }
    }
}

@Composable
fun CameraScreen(
    cameraSerial: String,
    cameraNo: Int,
    application: Application,
    activity: Activity
) {
    var player by remember { mutableStateOf<EZPlayer?>(null) }
    val localInfo: LocalInfo = LocalInfo.getInstance()

    var isSoundOpen by remember { mutableStateOf(localInfo.isSoundOpen) }
    var isTalking by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var currentVideoName by remember { mutableStateOf<String?>(null) }

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

    LaunchedEffect(Unit) {
        isSoundOpen = localInfo.isSoundOpen
    }

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
        }
    }

    fun stopCameraPreview() {
        player?.stopVoiceTalk()
        // 停止播放并释放播放器资源
        EZOpenSDK.getInstance().releasePlayer(player)
    }

    fun toggleSound() {
        if (isSoundOpen) {
            player?.closeSound()
        } else {
            player?.openSound()
        }
    }

    fun onClickTalk() {
        scope.launch {
            if(!isTalking) {
                player?.startVoiceTalk()
                delay(2000)
                player?.setVoiceTalkStatus(true)
                isTalking = true
            } else {
                player?.stopVoiceTalk()
                isTalking = false
            }
        }
    }

    fun onClickRecord() {
        if (isRecording) {
            if (player?.stopLocalRecord() == true) {
                scope.launch {
                    isRecording = false
                    if (currentVideoName != null) {
                        delay(2000)
                        addVideoToGalleryFromPrivateDir(context, File(currentVideoName).name)
                    }
                }
            }

            Log.d("ez record", "stop")
            return
        }

        currentVideoName =
            "${context.filesDir}/records/ZB_Camera_${cameraSerial}_${System.currentTimeMillis()}.mp4"
        Log.d("ez record", currentVideoName!!)
        if (player == null) {
            Log.d("ez record no player", currentVideoName!!)
            return
        }
        if (player!!.startLocalRecordWithFile(currentVideoName!!)) {
            isRecording = true
        } else {
            Toast.makeText(context, "启动录制失败", Toast.LENGTH_SHORT).show()
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
                            Log.d("surface destroyed", "123")
                            // 销毁时释放资源
                            stopCameraPreview()
                        }
                    })
                }
            }, modifier = Modifier.fillMaxSize()
        )

//        SmallFloatingActionButton(
//            onClick = {(context as? Activity)?.finish()},
//            containerColor = MaterialTheme.colorScheme.primary,
//            contentColor = MaterialTheme.colorScheme.secondary,
//            modifier = Modifier.align(Alignment.TopStart).offset(x = 10.dp, y = 10.dp)
//        ) {
//            Icon(Icons.Filled.ArrowBack, "退出", tint = Color.White)
//        }

        Box() {
            IconButton(
                onClick = {(context as? Activity)?.finish()},
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "退出",
                    tint = Color.White
                )
            }
        }
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

        Spacer(Modifier.height(100.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
//            Button(
//                onClick = {
//                    toggleSound()
//                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (isSoundOpen) Color.Green else Color.Red // 声音开：绿色，声音关：红色
//                ),
//            ) {
//                Text("声音")
//            }
            Button(
                onClick = {
                    onClickRecord()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) Color.Red else Color.LightGray,
                    contentColor = if (isRecording) Color.White else Color.White
                ),
                shape = CircleShape,
                modifier = Modifier.size(90.dp),
                border = BorderStroke(4.dp, if (isRecording) Color.Red else Color.Green)
            ) {
                Text(
                    text = if (isRecording) "录屏中" else "录屏",
//                    maxLines = 1,
//                    overflow = TextOverflow.Visible
                )
            }
            Button(
                onClick = {
                    onClickTalk()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTalking) Color.Blue else Color.LightGray,
                    contentColor = if (isTalking) Color.White else Color.White
                ),
                shape = CircleShape,
                modifier = Modifier.size(90.dp),
                border = BorderStroke(4.dp, if (isTalking) Color.Blue else Color.Blue)
            ) {
                Text(text = if (isTalking) "对讲中" else "对讲", maxLines = 1)
            }
            Button(
                onClick = {
                    player?.capturePicture().let {
                        if (it != null) {
                            val fileName =
                                "ZB_Camera_${cameraSerial}_${System.currentTimeMillis()}.png"
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                // Android 10+ 使用 MediaStore 保存图片
//                            saveBitmapToMediaStore(context, it, fileName)
                                saveBitmapToMediaStore(context, it, fileName)
                            } else {
                                // Android 9 及以下 需要申请存储权限并保存到外部存储
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                    == PackageManager.PERMISSION_GRANTED
                                ) {
                                    saveBitmapToPublicDir(context, it, fileName)
                                } else {
                                    ActivityCompat.requestPermissions(
                                        activity,
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        REQUEST_CODE
                                    )
                                }
                            }
                        }
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.White
                ),
                shape = CircleShape,
                modifier = Modifier.size(90.dp),
                border = BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
            ) {
                Text("截图")
            }
        }
    }
}
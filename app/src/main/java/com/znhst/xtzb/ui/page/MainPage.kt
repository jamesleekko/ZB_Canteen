package com.znhst.xtzb.ui.page


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.znhst.xtzb.R
import com.znhst.xtzb.viewModel.AuthViewModel
import com.journeyapps.barcodescanner.BarcodeCallback
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.activity.EZCameraActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(viewModel: AuthViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var scannedData by remember { mutableStateOf<String?>(null) }
    var showScanner by remember { mutableStateOf(false) }

    var isDialogVisible by remember { mutableStateOf(false) }

    // 请求摄像头权限
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d("权限", isGranted.toString())
        hasCameraPermission = isGranted
    }

    // 使用 LaunchedEffect 确保权限请求只在初始化完成后执行
    LaunchedEffect(key1 = Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasCameraPermission = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    BackHandler(enabled = showScanner) {
        if (showScanner) {
            // 当扫码界面显示时，按下返回按钮会将 showScanner 设置为 false
            showScanner = false
        } else {
            // 如果扫描界面已经隐藏，则调用默认返回事件
//            finish()  // 这是自定义的回调，调用系统默认行为
        }
    }

    fun startEzvizWebViewActivity() {
        val intent = Intent("com.videogo.main.EzvizWebViewActivity")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Optional: 如果在不同的任务中打开
        startActivity(context, intent, null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "智能化食堂") },
                actions = {
                    IconButton(onClick = {
                        if (hasCameraPermission) {
                            showScanner = true
                        } else {
                            Toast.makeText(context, "未获得相机权限", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.scan),
                            contentDescription = "扫描库存二维码",
                            tint = Color.Black
                        )
                    }
                },
            )
        },
        bottomBar = {
            BottomAppBar(content = {
                Button(onClick = {
                    logout(viewModel, navController)
                }) {
                    Text("注销")
                }

                Spacer(Modifier.width(16.dp))

                Button(onClick = {
                    val intent = Intent(context, EZCameraActivity::class.java)
                    startActivity(context, intent, null)
                }) {
                    Text("测试摄像头")
                }
            })
        },
    ) { innerPadding ->
        println(innerPadding)

        if (showScanner) {
            FullScreenScannerDialog(
                onDismiss = { showScanner = false },
                onScanned = { result ->
                    scannedData = result
                    isDialogVisible = true
                    showScanner = false
                }
            )
            QRCodeScanner { result ->
                scannedData = result
                isDialogVisible = true
                showScanner = false
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Scanned QR Code: $scannedData")
                if (isDialogVisible && scannedData != null) {
                    ScanResultDialog(
                        scannedResult = scannedData!!,
                        onDismiss = { isDialogVisible = false }
                    )
                }
            }
        }
    }
}

@Composable
fun FullScreenScannerDialog(
    onDismiss: () -> Unit,
    onScanned: (String) -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 全屏二维码扫描器
            QRCodeScanner { scannedData ->
                onScanned(scannedData) // 传递扫描结果并关闭对话框
            }

            // 可以在扫描界面上方放置返回按钮或其他 UI 元素
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun QRCodeScanner(onScanned: (String) -> Unit) {
    AndroidView(
        factory = { context ->
            val barcodeView = DecoratedBarcodeView(context).apply {
                decodeContinuous(object : BarcodeCallback {
                    override fun barcodeResult(result: com.journeyapps.barcodescanner.BarcodeResult?) {
                        result?.text?.let { scannedData ->
                            onScanned(scannedData)
                        }
                    }

                    override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>?) {
                        // 可选处理：可能的结果点
                    }
                })
            }
            barcodeView.resume() // 开启摄像头
            barcodeView
        },
        modifier = Modifier.fillMaxSize(),
        update = {
            it.resume() // 当视图重组时，确保相机继续工作
        }
    )
}

@Composable
fun ScanResultDialog(scannedResult: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        title = {
            Text(text = "扫描结果")
        },
        text = {
            Text(text = "Scanned QR Code: $scannedResult")
        }
    )
}

fun logout(viewModel: AuthViewModel, navController: NavController) {
    viewModel.logout {
        navController.navigate("login") {
            popUpTo("main") { inclusive = true }
        }
    }
}
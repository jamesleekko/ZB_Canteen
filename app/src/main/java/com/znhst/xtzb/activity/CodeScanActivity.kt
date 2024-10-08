package com.znhst.xtzb.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class CodeScanActivity:  ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CodeScanScreen()
        }
    }
}

@Composable
fun CodeScanScreen() {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var scannedData by remember { mutableStateOf<String?>(null) }
    var showScanner by remember { mutableStateOf(true) }
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

    Box(Modifier.fillMaxSize()) {
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
                    override fun barcodeResult(result: BarcodeResult?) {
                        result?.text?.let { scannedData ->
                            onScanned(scannedData)
                        }
                    }

                    override fun possibleResultPoints(resultPoints: List<ResultPoint>?) {
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
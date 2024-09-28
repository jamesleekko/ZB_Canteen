package com.znhst.xtzb.compose

import androidx.compose.runtime.Composable
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.ByteArrayInputStream

@Composable
fun Base64Image(base64String: String, modifier: Modifier) {
    // 去除前缀 "data:image/png;base64,"
    val cleanBase64String = base64String.substringAfter("base64,")

    // 解码 Base64 字符串为字节数组
    val imageBytes = Base64.decode(cleanBase64String, Base64.DEFAULT)

    // 将字节数组解码为 Bitmap
    val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(imageBytes))

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (bitmap != null) {
            // 将 Bitmap 转换为 ImageBitmap 并展示
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null, // 可以提供描述文字
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun IndeterminateCircularIndicator(modifier: Modifier) {
    CircularProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

//@Composable
//fun QRCodeScanner(scannerLauncher: ActivityResultLauncher<Intent>) {
//    Button(
//        onClick = {
//            scannerLauncher.launch(Intent(context, CaptureActivity::class.java))
//        },
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Text("扫描二维码")
//    }
//}
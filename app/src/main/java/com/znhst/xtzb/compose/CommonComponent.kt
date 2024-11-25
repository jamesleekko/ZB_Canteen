package com.znhst.xtzb.compose

import androidx.compose.runtime.Composable
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
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
//fun ZoomableBox(
//    modifier: Modifier = Modifier,
//    content: @Composable BoxScope.() -> Unit
//) {
//    var scale by remember { mutableStateOf(1f) }
//    var offsetX by remember { mutableStateOf(0f) }
//    var offsetY by remember { mutableStateOf(0f) }
//
//    Box(
//        modifier = modifier
//            .pointerInput(Unit) {
//                detectTransformGestures { _, pan, zoom, _ ->
//                    scale *= zoom
//                    scale = scale.coerceIn(1f, 5f) // 限制缩放范围
//
//                    offsetX += pan.x
//                    offsetY += pan.y
//                }
//            }
//            .graphicsLayer(
//                scaleX = scale,
//                scaleY = scale,
//                translationX = offsetX,
//                translationY = offsetY
//            )
//    ) {
//        content()
//    }
//}

@Composable
fun ZoomableBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 处理缩放或平移前的滚动事件
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // 处理滚动后事件
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    scale = scale.coerceIn(1f, 5f) // 限制缩放范围

                    offsetX += pan.x
                    offsetY += pan.y
                    println("Zoom: $zoom, Pan: $pan")
                }
            }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY
            )
    ) {
        content()
    }
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
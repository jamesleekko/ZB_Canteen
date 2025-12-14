package com.znhst.xtzb.compose

import androidx.compose.runtime.Composable
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.contentValuesOf
import java.io.ByteArrayInputStream

@Composable
fun Base64Image(
    base64String: String,
    modifier: Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
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
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    isPassword: Boolean = false,
) {
    val focusedColor = Color(0xFF4D66F3) // 聚焦状态的颜色 (RGB 77, 102, 243)
    val unfocusedColor = Color(0x1F000000) // 未聚焦状态的颜色 (RGBA 0, 0, 0, 0.12)
    var isFocused by remember { mutableStateOf(false) } // 用于跟踪焦点状态

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp) // 设置固定高度
            .clip(RoundedCornerShape(6.dp)) // 圆角
            .background(Color.Transparent) // 背景色（可调整）
            .border(
                width = 1.5.dp,
                color = if (isFocused) focusedColor else unfocusedColor, // 边框颜色动态变化
                shape = RoundedCornerShape(6.dp)
            )
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = 16.sp,
                lineHeight = 54.sp,
                modifier = Modifier.padding(16.dp,0.dp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, 12.dp) // 内间距
                .onFocusChanged { isFocused = it.isFocused }, // 检测焦点状态
            textStyle = LocalTextStyle.current.copy(
                fontSize = 18.sp,
                color = Color.Black,
                lineHeight = 34.sp
            ), // 字体大小
            cursorBrush = SolidColor(Color(0xFF4D66F3)),
            singleLine = true, // 单行输入
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}
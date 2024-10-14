package com.znhst.xtzb.compose

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.videogo.openapi.EZOpenSDK
import com.znhst.xtzb.R
import com.znhst.xtzb.utils.calculateDpValue
import kotlinx.coroutines.launch

@Composable
fun CircularRemoteControl(
    onUpDown: () -> Unit,
    onDownDown: () -> Unit,
    onLeftDown: () -> Unit,
    onRightDown: () -> Unit,
    onUpUp: () -> Unit,
    onDownUp: () -> Unit,
    onLeftUp: () -> Unit,
    onRightUp: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    // 半径可以根据需求调整
    val radius = 100.dp
    val centerButtonRadius = 30.dp
    val buttonSize = 70.dp
    val buttonOffset = centerButtonRadius + buttonSize/2
    val buttonColor = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent, // 默认背景透明
        contentColor = Color.White, // 文字或图标颜色为 primary
        disabledContainerColor = Color.Transparent,
        disabledContentColor = Color.White
    )

    Box(
        contentAlignment = Alignment.Center, // 将整个 Box 的内容居中
        modifier = Modifier
            .size(radius * 2) // 设置整个 Box 的大小（可以根据需求调整
            .shadow(8.dp, CircleShape)
            .background(Color.LightGray, CircleShape)
    ) {
        // 上
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = -buttonOffset)
                .size(buttonSize)
                .pointerInput(Unit) {
                    // 处理按下和抬起事件
                    detectTapGestures(
                        onPress = {
                            coroutineScope.launch {
                                onUpDown()
                                tryAwaitRelease()
                                onUpUp()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_up),
                contentDescription = "上移",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // 下
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = buttonOffset)
                .size(buttonSize)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            coroutineScope.launch {
                                onDownDown()
                                tryAwaitRelease()
                                onDownUp()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_down),
                contentDescription = "下移",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // 左
        Box(
            modifier = Modifier
                .offset(x = -buttonOffset, y = 0.dp)
                .size(buttonSize)
                .pointerInput(Unit) {
                    // 处理按下和抬起事件
                    detectTapGestures(
                        onPress = {
                            coroutineScope.launch {
                                onLeftDown()
                                tryAwaitRelease()
                                onLeftUp()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_left),
                contentDescription = "左移",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // 右
        Box(
            modifier = Modifier
                .offset(x = buttonOffset, y = 0.dp)
                .size(buttonSize)
                .pointerInput(Unit) {
                    // 处理按下和抬起事件
                    detectTapGestures(
                        onPress = {
                            coroutineScope.launch {
                                onRightDown()
                                tryAwaitRelease()
                                onRightUp()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.camera_right),
                contentDescription = "右移",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // 中间按钮（可以是确定或其他操作）
        Button(
            onClick = {},
            modifier = Modifier
                .size(centerButtonRadius * 2)
                .shadow(4.dp, CircleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {

        }
    }
}
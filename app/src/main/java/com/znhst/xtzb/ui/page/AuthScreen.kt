package com.znhst.xtzb.ui.page

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.R
import com.znhst.xtzb.compose.Base64Image
import com.znhst.xtzb.compose.IndeterminateCircularIndicator
import com.znhst.xtzb.compose.LoginTextField
import com.znhst.xtzb.viewModel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    context: Context,
    viewModel: AuthViewModel = viewModel(),
    navController: NavController
) {
    var bg = R.drawable.login_wallpaper

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var commonPadding = 16.dp

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        try {
            viewModel.getCaptcha()
        } catch (e: Exception) {
            Toast.makeText(context, "获取验证码失败！${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF1F8FF))
//            .windowInsetsPadding(WindowInsets.ime)
            .imePadding() // 自动调整避让键盘
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    // 隐藏键盘
                    focusManager.clearFocus()
                })
            },

        ) {

        Column(
            Modifier
                .fillMaxWidth()
//                .padding(commonPadding)
                .verticalScroll(rememberScrollState()) // 支持滚动
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
//                    .align(alignment = Alignment.TopCenter),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(360f / 301f)
                ) {
                    Image(
                        painter = painterResource(bg),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "智能化食堂",
                        fontSize = 32.sp,
                        color = Color(0xE0FFFFFF),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Text(
                        text = "众搏",
                        fontSize = 16.sp,
                        color = Color(0xE0FFFFFF),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 36.dp)
                    )
                }

                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(commonPadding)
                ) {
                    Spacer(Modifier.height(30.dp))

                    LoginTextField(
                        value = username,
                        onValueChange = { username = it },
                        placeholder = "用户名"
                    )

                    Spacer(modifier = Modifier.height(46.dp))

                    LoginTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "密码",
                        isPassword = true,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Base64Image(
                            viewModel.captchaState.value.img,
                            Modifier
                                .size(160.dp, 70.dp)
                                .clickable { scope.launch { viewModel.getCaptcha() } })
                        Spacer(modifier = Modifier.width(16.dp))
                        LoginTextField(
                            value = code,
                            onValueChange = { code = it },
                            placeholder = "校验码",
                        )
                    }
                }


            }

            Column(
                Modifier
                    .padding(commonPadding)
            ) {
                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            viewModel.login(username, password, code).onSuccess {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                                isLoading = false
                            }.onFailure {
                                viewModel.getCaptcha()
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "登录", fontSize = 20.sp,
                        letterSpacing = 4.sp,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        navController.navigate("register")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(77, 102, 243, 90)
                    )
                ) {
                    Text(
                        "注册",
                        fontSize = 20.sp,
                        letterSpacing = 4.sp,
                    )
                }

                Spacer(modifier = Modifier.height(56.dp))
            }
        }

        if (isLoading) {
            IndeterminateCircularIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(60.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AuthScreenPreview() {
//    AuthScreen()
//}
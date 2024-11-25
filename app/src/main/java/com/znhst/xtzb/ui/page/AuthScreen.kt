package com.znhst.xtzb.ui.page

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.compose.Base64Image
import com.znhst.xtzb.compose.IndeterminateCircularIndicator
import com.znhst.xtzb.viewModel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    context: Context,
    viewModel: AuthViewModel = viewModel(),
    navController: NavController
) {
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
        try{
            viewModel.getCaptcha()
        } catch(e: Exception) {
            Toast.makeText(context, "获取验证码失败！${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime)
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
                .padding(commonPadding)
                .align(alignment = Alignment.TopCenter),
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                "智能化食堂",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("用户名") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (!it.isFocused) {
                            keyboardController?.hide()
                        }
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Base64Image(
                    viewModel.captchaState.value.img,
                    Modifier
                        .size(160.dp, 70.dp)
                        .clickable { scope.launch { viewModel.getCaptcha() } })
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("校验码") },
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (!it.isFocused) {
                                keyboardController?.hide()
                            }
                        }
                )
            }
        }

        Column(
            Modifier
                .padding(commonPadding)
                .align(Alignment.BottomCenter),
        ) {
            Button(
                onClick = {
                    navController.navigate("register")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    "注册",
                    fontSize = 20.sp,
                    letterSpacing = 4.sp,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    "登录", fontSize = 20.sp,
                    letterSpacing = 4.sp,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
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
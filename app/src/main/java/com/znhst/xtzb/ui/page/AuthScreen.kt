package com.znhst.xtzb.ui.page

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.R
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
    val bg = R.drawable.login_wallpaper

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var code by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val canSubmit by remember {
        derivedStateOf {
            username.isNotBlank() && password.isNotBlank() && code.isNotBlank() && !isLoading
        }
    }

    LaunchedEffect(Unit) {
        try {
            viewModel.getCaptcha().onFailure {
                snackbarHostState.showSnackbar(
                    message = "获取验证码失败：${it.message ?: "请稍后重试"}"
                )
            }
        } catch (e: Exception) {
            snackbarHostState.showSnackbar(
                message = "获取验证码失败：${e.message ?: "请稍后重试"}"
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { contentPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding)
                .imePadding()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
        ) {
            // 顶部背景图 + 渐变遮罩
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .statusBarsPadding(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "智能化食堂",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "众搏 · 账号登录",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(200.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "欢迎回来",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "请输入账号信息完成登录",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("用户名") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Text
                            ),
                        )

                        Spacer(Modifier.height(12.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            label = { Text("密码") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                            trailingIcon = {
                                TextButton(
                                    onClick = { passwordVisible = !passwordVisible },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text(text = if (passwordVisible) "隐藏" else "显示")
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                                keyboardType = KeyboardType.Password
                            ),
                        )

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier
                                    .width(150.dp)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        scope.launch {
                                            try {
                                                viewModel.getCaptcha().onFailure {
                                                    snackbarHostState.showSnackbar(
                                                        message = "刷新验证码失败：${it.message ?: "请稍后重试"}"
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                snackbarHostState.showSnackbar(
                                                    message = "刷新验证码失败：${e.message ?: "请稍后重试"}"
                                                )
                                            }
                                        }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Base64Image(
                                        viewModel.captchaState.value.img,
                                        Modifier.fillMaxSize(),
                                        contentScale = ContentScale.FillBounds
                                    )
                                }
                            }

                            Spacer(Modifier.width(10.dp))

                            OutlinedTextField(
                                value = code,
                                onValueChange = { code = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                singleLine = true,
                                placeholder = { Text("验证码") },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Number
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    }
                                ),
                            )

                            Spacer(Modifier.width(6.dp))

                            IconButton(
                                modifier = Modifier
                                    .size(56.dp),
                                onClick = {
                                    scope.launch {
                                        try {
                                            viewModel.getCaptcha().onFailure {
                                                snackbarHostState.showSnackbar(
                                                    message = "刷新验证码失败：${it.message ?: "请稍后重试"}"
                                                )
                                            }
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar(
                                                message = "刷新验证码失败：${e.message ?: "请稍后重试"}"
                                            )
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Filled.Refresh, contentDescription = "刷新验证码")
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                keyboardController?.hide()

                                if (username.isBlank() || password.isBlank() || code.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("请完整填写用户名、密码和验证码")
                                    }
                                    return@Button
                                }

                                isLoading = true
                                scope.launch {
                                    viewModel.login(username, password, code)
                                        .onSuccess {
                                            navController.navigate("main") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                        .onFailure { err ->
                                            try {
                                                viewModel.getCaptcha()
                                            } catch (_: Exception) {
                                                // ignore
                                            }
                                            snackbarHostState.showSnackbar(
                                                message = err.message ?: "登录失败，请稍后重试"
                                            )
                                        }
                                    isLoading = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = canSubmit
                        ) {
                            Text(
                                text = if (isLoading) "正在登录…" else "登录",
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { navController.navigate("register") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !isLoading
                        ) {
                            Text(
                                text = "注册",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                Text(
                    text = "点击验证码图片或右侧按钮可刷新",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))
            }

            if (isLoading) {
                IndeterminateCircularIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun AuthScreenPreview() {
//    AuthScreen()
//}
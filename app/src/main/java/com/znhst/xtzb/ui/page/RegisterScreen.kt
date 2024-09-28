package com.znhst.xtzb.ui.page

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.compose.IndeterminateCircularIndicator
import com.znhst.xtzb.utils.ValidationUtils
import com.znhst.xtzb.viewModel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(viewModel: AuthViewModel = viewModel(), navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context: Context = LocalContext.current

    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    fun validInput():Boolean {
        if(!ValidationUtils.validUserName(context, username)) return false
        if(!ValidationUtils.validPassword(context, password)) return false
        if(!ValidationUtils.doPasswordsMatch(context, password, confirmPassword)) return false
        if(!ValidationUtils.validPhone(context, phone)) return false
        return true
    }

    fun register() {
        if(!validInput()){
            return
        }

        scope.launch {
            isLoading = true
            viewModel.register(username, password, phone).onSuccess {
                isLoading = false
                Toast.makeText(context, "注册成功！", Toast.LENGTH_SHORT).show()
                delay(2000)
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }.onFailure { isLoading = false }
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
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Spacer(Modifier.height(32.dp))

            Text(
                "注册",
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

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("密码") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("确认密码") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("联系电话") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    register()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("提交", fontSize = 20.sp, letterSpacing = 4.sp)
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
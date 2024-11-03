package com.znhst.xtzb.ui.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.znhst.xtzb.viewModel.AuthViewModel
import com.znhst.xtzb.viewModel.ProfileViewModel

val baseUrl = "http://10.0.2.2:8000"

@Composable
fun Profile(
    viewModel: AuthViewModel = viewModel(),
    outNavController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
//    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        profileViewModel.fetchUserInfo()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        UserInfo(profileViewModel)

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                logout(viewModel, outNavController)
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("退出登录")
            }
        }
    }
}

@Composable
fun UserInfo(viewModel: ProfileViewModel) {
    val userInfo = viewModel.userInfo.value
    val avatarUrl = remember(userInfo.avatarName) {
        "${baseUrl}/avatar/${userInfo.avatarName}"
    }

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(Modifier.height(16.dp))

        // 头像显示 - 方形圆角
        AsyncImage(
            model = avatarUrl,
            contentDescription = "头像",
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
        )

        Spacer(Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            // 用户信息
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                UserInfoRow(label = "登录账号", value = userInfo.userName, showDivider = true)
                UserInfoRow(label = "用户昵称", value = userInfo.nickName, showDivider = true)
                UserInfoRow(label = "所属部门", value = userInfo.dept.name, showDivider = true)
                UserInfoRow(label = "手机号码", value = userInfo.phone, showDivider = false)
            }
        }
    }
}

@Composable
fun UserInfoRow(label: String, value: String, showDivider: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            modifier = Modifier.weight(0.4f),
            fontSize = 16.sp,
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.6f),
            color = Color.DarkGray,
            fontSize = 16.sp,
            textAlign = TextAlign.End
        )
    }
    Spacer(Modifier.height(4.dp))
    if (showDivider) HorizontalDivider(
        color = Color.LightGray.copy(alpha = 0.5f), // 设置所需颜色
        thickness = 1.dp
    )
}

fun logout(viewModel: AuthViewModel, navController: NavController) {
    viewModel.logout {
        navController.navigate("login") {
            popUpTo("main") { inclusive = true }
        }
    }
}
package com.znhst.xtzb.ui.page

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.znhst.xtzb.BuildConfig
import com.znhst.xtzb.ui.theme.GradientEnd
import com.znhst.xtzb.ui.theme.GradientStart
import com.znhst.xtzb.viewModel.AuthViewModel
import com.znhst.xtzb.viewModel.ProfileViewModel

@Composable
fun Profile(
    viewModel: AuthViewModel = viewModel(),
    outNavController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        profileViewModel.fetchUserInfo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(profileViewModel)

        Spacer(modifier = Modifier.height(16.dp))

        UserInfoCard(profileViewModel)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { logout(viewModel, outNavController) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF04438).copy(alpha = 0.1f),
                contentColor = Color(0xFFF04438)
            )
        ) {
            Text(
                "退出登录",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun ProfileHeader(viewModel: ProfileViewModel) {
    val userInfo = viewModel.userInfo.value
    val avatarUrl = remember(userInfo.avatarName) {
        "${BuildConfig.BASE_URL}/avatar/${userInfo.avatarName}"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(GradientStart, GradientEnd)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "头像",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = userInfo.nickName.ifEmpty { userInfo.userName },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun UserInfoCard(viewModel: ProfileViewModel) {
    val userInfo = viewModel.userInfo.value

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "个人信息",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(16.dp))

            UserInfoRow(label = "登录账号", value = userInfo.userName, showDivider = true)
            UserInfoRow(label = "用户昵称", value = userInfo.nickName, showDivider = true)
            UserInfoRow(label = "所属园区", value = userInfo.dept.name, showDivider = true)
            UserInfoRow(label = "手机号码", value = userInfo.phone, showDivider = false)
        }
    }
}

@Composable
fun UserInfoRow(label: String, value: String, showDivider: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
    if (showDivider) HorizontalDivider(
        color = MaterialTheme.colorScheme.outlineVariant,
        thickness = 0.5.dp
    )
}

@Composable
fun DisplayColorScheme() {
    val colorScheme = MaterialTheme.colorScheme
    Column {
        Text("Primary", color = colorScheme.primary)
        Text("OnPrimary", color = colorScheme.onPrimary)
        Text("PrimaryContainer", color = colorScheme.primaryContainer)
        Text("OnPrimaryContainer", color = colorScheme.onPrimaryContainer)
        Text("Secondary", color = colorScheme.secondary)
        Text("OnSecondary", color = colorScheme.onSecondary)
        Text("SecondaryContainer", color = colorScheme.secondaryContainer)
        Text("OnSecondaryContainer", color = colorScheme.onSecondaryContainer)
        Text("Tertiary", color = colorScheme.tertiary)
        Text("OnTertiary", color = colorScheme.onTertiary)
        Text("Background", color = colorScheme.background)
        Text("OnBackground", color = colorScheme.onBackground)
        Text("Surface", color = colorScheme.surface)
        Text("OnSurface", color = colorScheme.onSurface)
    }
}

fun logout(viewModel: AuthViewModel, navController: NavController) {
    viewModel.logout {
        val currentDestination = navController.currentDestination
        if(currentDestination?.route != "login") {
            navController.navigate("login") {
                popUpTo("main") { inclusive = true }
            }
        }
    }
}

package com.znhst.xtzb.ui.page


import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.znhst.xtzb.R
import com.znhst.xtzb.viewModel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.znhst.xtzb.activity.CodeScanActivity
import com.znhst.xtzb.activity.EZCameraActivity

sealed class MainScreenRoute(
    val route: String,
    val title: String,
    val iconId: Int,
) {
    object News : MainScreenRoute("news", "信息墙", R.drawable.list_alt)
    object Devices : MainScreenRoute("devices", "设备视察", R.drawable.eye_tracking)
    object Profile : MainScreenRoute("profile", "我的信息", R.drawable.person)
}

val routes = listOf(
    MainScreenRoute.News,
    MainScreenRoute.Devices,
    MainScreenRoute.Profile,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(viewModel: AuthViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "智能化食堂") },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(context, CodeScanActivity::class.java)
                        startActivity(context, intent, null)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.scan),
                            contentDescription = "扫描库存二维码",
                            tint = Color.Black
                        )
                    }
                },
            )
        },
        bottomBar = {
//            BottomAppBar(content = {
//                Button(onClick = {
//                    logout(viewModel, navController)
//                }) {
//                    Text("注销")
//                }
//
//                Spacer(Modifier.width(16.dp))
//
//                Button(onClick = {
//                    val intent = Intent(context, EZCameraActivity::class.java)
//                    startActivity(context, intent, null)
//                }) {
//                    Text("测试摄像头")
//                }
//            })
            NavigationBar {
                routes.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = screen.iconId),
                                contentDescription = screen.route
                            )
                        },
                        label = { Text(screen.title) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(screen.route)
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        println(innerPadding)

        Column(Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = MainScreenRoute.News.route) {
                composable(MainScreenRoute.News.route) { News() }
                composable(MainScreenRoute.Devices.route) { DeviceList() }
                composable(MainScreenRoute.Profile.route) { Profile(viewModel) }
            }
        }
    }
}
package com.znhst.xtzb.ui.page

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.znhst.xtzb.R
import com.znhst.xtzb.viewModel.AuthViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.znhst.xtzb.activity.CodeScanActivity
import com.znhst.xtzb.dataModel.TempHumiCategory
import com.znhst.xtzb.viewModel.DeviceViewModel

sealed class MainScreenRoute(
    val route: String,
    val title: String,
    val iconId: Int,
) {
    object News : MainScreenRoute("news", "信息墙", R.drawable.list_alt)
    object Devices : MainScreenRoute("device_category", "设备视察", R.drawable.eye_tracking)
    object Profile : MainScreenRoute("profile", "我的信息", R.drawable.person)
}

val routes = listOf(
    MainScreenRoute.News,
    MainScreenRoute.Devices,
    MainScreenRoute.Profile,
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    viewModel: AuthViewModel = viewModel(),
    deviceViewModel: DeviceViewModel = viewModel(),
    outNavController: NavController
) {
    val context = LocalContext.current
    val mainPageNavController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }

    var canNavigateBack by remember { mutableStateOf(false) }
    var currentRoute by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(mainPageNavController) {
        mainPageNavController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentRoute = backStackEntry.destination.route
            canNavigateBack =
                mainPageNavController.previousBackStackEntry != null && currentRoute != "news" && currentRoute != "device_category" && currentRoute != "profile"
        }
    }

    Scaffold(
        topBar = {
            val navigationIcon: (@Composable () -> Unit) = run {
                {
                    if (canNavigateBack) {
                        IconButton(onClick = {
                            mainPageNavController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "返回",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
            TopAppBar(
                title = { Text(text = "智能化食堂") },
                navigationIcon = navigationIcon,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = if (outNavController.currentDestination?.route == "main") 0.dp else 2.dp,
                        shape = RectangleShape,
                        clip = true
                    )
            )
        },
        bottomBar = {
            NavigationBar {
                routes.forEachIndexed { index, screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = screen.iconId),
                                contentDescription = screen.route
                            )
                        },
                        label = { Text(screen.title, fontSize = 14.sp) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            mainPageNavController.navigate(screen.route)
                        }
                    )
                }
            }
        },
    ) { innerPadding ->
        println(innerPadding)

        Column(Modifier.padding(innerPadding)) {
            NavHost(
                navController = mainPageNavController,
                startDestination = MainScreenRoute.News.route
            ) {
                composable(MainScreenRoute.News.route) { News(outNavController = outNavController) }
                composable(MainScreenRoute.Devices.route) {
                    DeviceCategory(
                        deviceViewModel,
                        navController = mainPageNavController
                    )
                }
                composable(MainScreenRoute.Profile.route) { Profile(viewModel, outNavController) }
                composable("camera_list") { CameraList(navController = mainPageNavController) }
                composable(
                    route = "door_list"
                ) {
                    DoorList(navController = mainPageNavController)
                }
                composable(
                    route = "stock"
                ) {
                    StockScreen(navController = mainPageNavController)
                }
                composable(
                    route = "freezer_list"
                ) {
                    FreezerList(navController = mainPageNavController)
                }
                composable(
                    route = "smoke_alarm_list"
                ) {
                    SmokeAlarmList(navController = mainPageNavController)
                }
                composable(
                    route = "temp_humi_list/{category}",
                    arguments = listOf(
                        navArgument("category") { type = NavType.StringType } // 将枚举传递为字符串
                    )
                ) { backStackEntry ->
                    val categoryString = backStackEntry.arguments?.getString("category")
                    val category = TempHumiCategory.valueOf(categoryString!!) // 将字符串转回枚举
                    TempHumiList(navController = mainPageNavController, category = category)
                }
                composable(
                    route = "freezer_detail/{deviceNo}",
                    arguments = listOf(navArgument("deviceNo") { type = NavType.StringType })
                ) { backStackEntry ->
                    val deviceNo = backStackEntry.arguments?.getString("deviceNo")
                    FreezerDetail(deviceNo = deviceNo!!)
                }
                composable(
                    route = "smoke_alarm_detail/{deviceNo}",
                    arguments = listOf(navArgument("deviceNo") { type = NavType.StringType })
                ) { backStackEntry ->
                    val deviceNo = backStackEntry.arguments?.getString("deviceNo")
                    SmokeAlarmDetail(deviceNo = deviceNo!!)
                }
                composable(
                    route = "door_detail/{doorGuid}",
                    arguments = listOf(navArgument("doorGuid") { type = NavType.StringType })
                ) { backStackEntry ->
                    val doorGuid = backStackEntry.arguments?.getString("doorGuid")
                    DoorDetail(doorGuid = doorGuid!!, mainPageNavController)
                }
                composable(
                    route = "temp_humi_detail/{deviceNo}",
                    arguments = listOf(navArgument("deviceNo") { type = NavType.StringType })
                ) { backStackEntry ->
                    val deviceNo = backStackEntry.arguments?.getString("deviceNo")
                    TempHumiDetail(deviceNo = deviceNo!!)
                }
                composable(
                    route = "vr"
                ) {
                    VR(navController = mainPageNavController)
                }
            }
        }
    }
}
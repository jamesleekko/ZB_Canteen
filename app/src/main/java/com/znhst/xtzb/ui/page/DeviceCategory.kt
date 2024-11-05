package com.znhst.xtzb.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.znhst.xtzb.R
import com.znhst.xtzb.dataModel.EZDeviceCategory
import com.znhst.xtzb.viewModel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val deviceTypeDrawableMap: Map<Int, Int> = mapOf(
    1 to R.drawable.device_camera,  // Replace with actual drawable resource
    2 to R.drawable.device_door,
    3 to R.drawable.device_freezer,
    4 to R.drawable.device_virus,
    5 to R.drawable.device_smoke_alarm,
    6 to R.drawable.device_stock,
    7 to R.drawable.device_food_detection,
    8 to R.drawable.device_screen,
    9 to R.drawable.device_ai_face,
    10 to R.drawable.device_temp,
    11 to R.drawable.device_temp,
    12 to R.drawable.device_360
)

fun getDeviceDrawable(type: Int): Int {
    return deviceTypeDrawableMap[type] ?: R.drawable.device_hub // Default icon if type not found
}

@Composable
fun DeviceCategory(deviceViewModel: DeviceViewModel, navController: NavController) {
    val context = LocalContext.current
    val deviceCategories by deviceViewModel.categoryList.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            deviceViewModel.fetchCategories()
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // 2 columns
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(deviceCategories.size) { index ->
            val category = deviceCategories[index]
            DeviceCategoryItem(category, navController)
        }
    }
}

@Composable
fun DeviceCategoryItem(category: EZDeviceCategory, navController: NavController) {
    fun onClickCard(type: Int) {
        when (type) {
            1 -> {
                navController.navigate("camera_list")
            }

            3 -> {
                navController.navigate("freezer_list")
            }

            5 -> {
                navController.navigate("smoke_alarm_list")
            }
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .size(150.dp)
            .clickable { onClickCard(category.type) }) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color(242, 237, 246,1))
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = getDeviceDrawable(category.type)),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.DarkGray)
                )
                Spacer(Modifier.height(8.dp))
                Text(text = category.displayName)
            }
        }
    }
}
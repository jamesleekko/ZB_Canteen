package com.znhst.xtzb.ui.page

import android.content.Intent
import android.util.Log
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.znhst.xtzb.R
import com.znhst.xtzb.activity.VRActivity
import com.znhst.xtzb.dataModel.EZDeviceCategory
import com.znhst.xtzb.dataModel.TempHumiCategory
import com.znhst.xtzb.viewModel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val deviceTypeDrawableMap: Map<Int, Int> = mapOf(
    1 to R.drawable.device_camera,
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

val deviceCardGradients: List<List<Color>> = listOf(
    listOf(Color(0xFF667eea), Color(0xFF764ba2)),
    listOf(Color(0xFF43e97b), Color(0xFF38f9d7)),
    listOf(Color(0xFFfa709a), Color(0xFFfee140)),
    listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
    listOf(Color(0xFFa18cd1), Color(0xFFfbc2eb)),
    listOf(Color(0xFFfccb90), Color(0xFFd57eeb)),
    listOf(Color(0xFF667eea), Color(0xFF764ba2)),
    listOf(Color(0xFF43e97b), Color(0xFF38f9d7)),
    listOf(Color(0xFFfa709a), Color(0xFFfee140)),
    listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
    listOf(Color(0xFFa18cd1), Color(0xFFfbc2eb)),
    listOf(Color(0xFFfccb90), Color(0xFFd57eeb)),
)

fun getDeviceDrawable(type: Int): Int {
    return deviceTypeDrawableMap[type] ?: R.drawable.device_hub
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
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(deviceCategories.size) { index ->
            val category = deviceCategories[index]
            DeviceCategoryItem(category, navController, index)
        }
    }
}

@Composable
fun DeviceCategoryItem(category: EZDeviceCategory, navController: NavController, index: Int) {
    val context = LocalContext.current
    val gradient = deviceCardGradients[index % deviceCardGradients.size]

    fun onClickCard(type: Int) {
        when (type) {
            1 -> navController.navigate("camera_list")
            2 -> navController.navigate("door_list")
            3 -> navController.navigate("freezer_list")
            5 -> navController.navigate("smoke_alarm_list")
            6 -> navController.navigate("stock")
            11 -> navController.navigate("temp_humi_list/${TempHumiCategory.LAB.name}")
            12 -> {
                val intent = Intent(context, VRActivity::class.java)
                context.startActivity(intent)
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClickCard(category.type) },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(gradient)
                )
                .padding(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = getDeviceDrawable(category.type)),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = category.displayName,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

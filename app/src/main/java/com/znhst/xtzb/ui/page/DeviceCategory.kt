package com.znhst.xtzb.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.videogo.openapi.bean.EZDeviceInfo
import com.znhst.xtzb.dataModel.EZDeviceCategory
import com.znhst.xtzb.utils.TokenManager
import com.znhst.xtzb.viewModel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            .padding(16.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
            .height(100.dp)
            .clickable { onClickCard(category.type) }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column {
                Text(text = category.displayName)
            }
        }
    }
}
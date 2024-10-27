package com.znhst.xtzb.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.viewModel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SmokeAlarmList(deviceViewModel: DeviceViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val deviceList by deviceViewModel.smokeAlarmList.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            deviceViewModel.fetchSmokeAlarms()
        }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(deviceList) { device ->
            Card(modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate("freezer_detail/${device.deviceNo}")
                }) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "设备名称: ${device.deviceAlias}")
                    Text(text = "设备编号: ${device.deviceNo}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
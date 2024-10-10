package com.znhst.xtzb.ui.page

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.bean.EZDeviceInfo
import com.znhst.xtzb.activity.EZCameraActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DeviceList() {
    val context = LocalContext.current
    val instance = EZOpenSDK.getInstance()
    var deviceList = remember { mutableStateListOf<EZDeviceInfo>() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            deviceList.addAll(instance.getDeviceList(0, 100))
        }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(deviceList) { device ->
            Card(modifier = Modifier.fillMaxWidth(),
                onClick = {
                val intent = Intent(context, EZCameraActivity::class.java).apply {
                    putExtra("cameraSerial", device.deviceSerial)
                    putExtra("cameraNo", device.cameraNum)
                }
                startActivity(context, intent, null)
            }) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "设备名称: ${device.deviceName}")
                    Text(text = "序列号: ${device.deviceSerial}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
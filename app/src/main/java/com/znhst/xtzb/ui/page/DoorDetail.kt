package com.znhst.xtzb.ui.page

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.viewModel.DoorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val openWayMap: Map<Int, String> = mapOf(
    1 to "二维码",
    2 to "刷卡",
    3 to "人脸识别",
    4 to "密码",
    5 to "扫码开门",
    9 to "远程开门"
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoorDetail(
    doorGuid: String,
    navController: NavController,
    viewModel: DoorViewModel = viewModel()
) {

    val historyList by viewModel.historyList.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.fetchHistory(doorGuid, 1, 100, null, null)
        }
    }

    Box() {
        Box(Modifier.offset(y = (-4).dp)) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "退出",
                    tint = Color.Black
                )
            }
        }

        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 56.dp)) {
            itemsIndexed(historyList) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "操作时间: ${item.visitTime}")
                        Spacer(Modifier.height(8.dp))
                        Text(text = "人员：${item.memberXm}")
                        Spacer(Modifier.height(8.dp))
                        Text(text = item.remark)
                        Spacer(Modifier.height(8.dp))
                        Text(text = openWayMap[item.openWay] ?: "未知方式")
                    }
                }

                if (index < historyList.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}
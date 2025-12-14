package com.znhst.xtzb.ui.page

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.network.DoorOperationEntry
import com.znhst.xtzb.viewModel.DoorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    val isLoading by viewModel.isLoading.collectAsState()

    val displayList = if (!isLoading && historyList.isEmpty()) {
        mockDoorHistory(doorGuid)
    } else {
        historyList
    }

    // 获取历史记录
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.fetchHistory(doorGuid, 1, 100, null, null)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } 
        // else if (historyList.isEmpty()) {
        //     Box(
        //         modifier = Modifier.fillMaxSize(),
        //         contentAlignment = Alignment.Center
        //     ) {
        //         Text(
        //             text = "暂无数据",
        //             style = MaterialTheme.typography.bodyLarge,
        //             color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        //         )
        //     }
        // }
         else {
            // 历史记录列表
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(displayList) { index, item ->
                // 单个记录卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "操作时间: ${item.visitTime}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "人员: ${item.memberXm}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.remark,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = openWayMap[item.openWay] ?: "未知方式",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
            }
        }

        // 返回按钮
//        IconButton(
//            onClick = { navController.popBackStack() },
//            modifier = Modifier
//                .padding(16.dp)
//                .size(36.dp)
//                .clip(CircleShape)
//                .background(MaterialTheme.colorScheme.primary)
//                .align(Alignment.TopStart)
//                .shadow(elevation = 4.dp, shape = CircleShape)
//        ) {
//            Icon(
//                imageVector = Icons.Default.ArrowBack,
//                contentDescription = "返回",
//                tint = Color.White
//            )
//        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun mockDoorHistory(doorGuid: String): List<DoorOperationEntry> {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val now = LocalDateTime.now()
    val openWays = listOf(1, 2, 3, 4, 5, 9)
    val names = listOf("admin001", "admin002", "admin003", "admin004", "admin005", "admin006")
    val remarks = listOf("测试数据")

    return List(20) { idx ->
        val t = now.minusMinutes((idx * 7L) + 3L)
        DoorOperationEntry(
            logSn = "mock-${idx + 1}",
            doorGuid = doorGuid,
            doorName = "门禁-${doorGuid.take(6)}",
            memberXm = names[idx % names.size],
            visitTime = t.format(formatter),
            imgUrl = null,
            openWay = openWays[idx % openWays.size],
            openStatus = if (idx % 6 == 0) 2 else 1,
            remark = remarks[idx % remarks.size]
        )
    }
}
package com.znhst.xtzb.ui.page

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.znhst.xtzb.R
import com.znhst.xtzb.viewModel.SmokeAlarmViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SmokeAlarmDetail(deviceNo: String, viewModel: SmokeAlarmViewModel = viewModel()) {
    val historyList by viewModel.historyList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val alarmImgId = R.drawable.detector_alarm
    val normalImgId = R.drawable.detector_fine

    // 获取最新状态
    val latestStatus = historyList.firstOrNull()?.doorStatus
    // 当无数据时，默认显示为正常状态
    val isNormal = latestStatus?.let { it == "正常" } ?: true

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.fetchHistory(deviceNo)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // 当前状态栏
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isNormal) Color(0xFFE8F5E9) else Color(
                        0xFFFFEBEE
                    )
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = if (isNormal) normalImgId else alarmImgId),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        colorFilter = ColorFilter.tint(
                            if (isNormal) Color(0xFF4CAF50) else Color(
                                0xFFF44336
                            )
                        )
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "当前状态",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNormal) Color(0xFF388E3C) else Color(0xFFD32F2F)
                        )
                        Text(
                            text = when {
                                latestStatus == null -> "设备正常运行"
                                isNormal -> "设备正常运行"
                                else -> "警报触发"
                            },
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if(!isLoading) {
            Text(text = "历史记录", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(4.dp))
            
            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        }
        
        if (!isLoading && historyList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(historyList) { index, item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),

                    ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = if (item.doorStatus == "正常") normalImgId else alarmImgId),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            colorFilter = ColorFilter.tint(Color.DarkGray)
                        )
                        Column(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = "触发时间: ${item.time}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                            Text(
                                text = "状态: ${item.doorStatus}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (item.doorStatus == "正常") Color(0xFF4CAF50) else Color(
                                    0xFFF44336
                                )
                            )
                        }
                    }
                }

                if (index < historyList.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray
                    )
                }
            }
        }
    }}
}
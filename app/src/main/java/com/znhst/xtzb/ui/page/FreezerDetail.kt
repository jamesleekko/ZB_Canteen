package com.znhst.xtzb.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.znhst.xtzb.R
import com.znhst.xtzb.viewModel.FreezerDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FreezerDetail(deviceNo: String, viewModel: FreezerDetailViewModel = viewModel()) {
    val historyList by viewModel.historyList.collectAsState()
    val openDoorImgId = R.drawable.door_open
    val closeDoorImgId = R.drawable.door_close

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.fetchHistory(deviceNo)
        }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        itemsIndexed(historyList) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = if (item.doorStatus == "开门") openDoorImgId else closeDoorImgId),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(Color.DarkGray)
                )
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "操作时间: ${item.time}")
                    Spacer(Modifier.height(8.dp))
                    Text(text = "动作: ${item.doorStatus}")
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
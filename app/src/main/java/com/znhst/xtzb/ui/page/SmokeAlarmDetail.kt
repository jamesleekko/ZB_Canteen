package com.znhst.xtzb.ui.page

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.znhst.xtzb.viewModel.FreezerDetailViewModel
import com.znhst.xtzb.viewModel.SmokeAlarmViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SmokeAlarmDetail(deviceNo: String, viewModel: SmokeAlarmViewModel = viewModel()) {
    val historyList by viewModel.historyList.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.fetchHistory(deviceNo)
        }
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(historyList) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = "操作时间: ${item.time}")
                    Text(text = "动作: ${item.doorStatus}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
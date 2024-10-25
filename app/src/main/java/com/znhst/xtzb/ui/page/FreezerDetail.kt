package com.znhst.xtzb.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.znhst.xtzb.viewModel.FreezerDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FreezerDetail(deviceNo: String, viewModel: FreezerDetailViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            viewModel.fetchHistory(deviceNo)
        }
    }

    Column {
        Button(onClick = {}) {
            Text("冰箱！")
        }
    }
}
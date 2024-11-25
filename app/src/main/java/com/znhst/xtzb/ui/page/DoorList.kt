package com.znhst.xtzb.ui.page

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.viewModel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DoorList(deviceViewModel: DeviceViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val doorList by deviceViewModel.doorList.collectAsState()

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            deviceViewModel.fetchDoors()
        }
    }

    Box {
        Box(Modifier.offset(y = (-4).dp)) {
            IconButton(
                onClick = {navController.popBackStack()},
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
            items(doorList) { door ->
                Card(modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("door_detail/${door.doorGuid}")
                    }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = door.doorName, fontSize = 20.sp)
                            Spacer(Modifier.height(8.dp))
                            Text(text = door.doorGuid, fontStyle = FontStyle.Italic)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Canvas(modifier = Modifier.size(12.dp)) {
                                drawCircle(
                                    color = if (door.doorStatus == 1) Color.Green else Color.Gray // 在线为绿色，离线为灰色
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = if (true) "在线" else "离线")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
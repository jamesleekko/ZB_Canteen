package com.znhst.xtzb.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.znhst.xtzb.dataModel.Stock
import com.znhst.xtzb.dataModel.StockItem
import com.znhst.xtzb.viewModel.StockViewModel



@Composable
fun StockScreen(viewModel: StockViewModel = viewModel(), navController: NavController) {
    val stockItems by viewModel.stockItems.observeAsState(emptyList())
    val stocks by viewModel.stocks.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.loadStockItems()
        viewModel.loadStocks()
    }

    Box{
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

        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 56.dp)) {
            LazyColumn {
                items(stocks) { item ->
                    StockRow(item)
                }
            }
        }
    }
}

@Composable
fun StockItemRow(item: StockItem) {
    Row(modifier = Modifier.padding(8.dp)) {
        Text(item.name, style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.weight(1f))
        Text("${item.category} - ${item.unit}", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun StockRow(stock: Stock) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp) // 增加 padding
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stock.item.name,
                style = MaterialTheme.typography.titleMedium, // 使用更大的字体
                fontSize = 20.sp // 设置更大字体大小
            )
            Text(
                text = "${stock.item.category.toChinese()} - ${stock.item.unit.toChinese()}",
                style = MaterialTheme.typography.bodyLarge, // 使用较大正文字体
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "库存量: ${stock.currentQuantity}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "更新于: ${stock.lastUpdated}",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp, // 较小的说明文字
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
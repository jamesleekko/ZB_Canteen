package com.znhst.xtzb.ui.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun News(outNavController: NavController) {
    val navController = rememberNavController()
    var currentPlate by remember { mutableStateOf("common") } //common | local

    fun onClickCommon() {
        currentPlate = "common"
        navController.navigate("common_news")
    }

    fun onClickLocal() {
        currentPlate = "local"
        navController.navigate("local_news")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TopStickyHeader(
            currentPlate,
            onClickCommon = { onClickCommon() },
            onClickLocal = { onClickLocal() })

        Spacer(modifier = Modifier.height(56.dp))

        NavHost(
            navController = navController,
            startDestination = "common_news",
            modifier = Modifier.padding(top = 56.dp)
        ) {
            composable("common_news") {
                CommonNews(navController = navController, outNavController = outNavController)
            }
            composable("local_news") {
                LocalNews(navController = navController, outNavController = outNavController)
            }
        }
    }

}

@Composable
fun TopStickyHeader(currentPlate: String, onClickCommon: () -> Unit, onClickLocal: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Row() {
                TextButton(onClick = { onClickCommon() }) {
                    Text(
                        "公共",
                        fontSize = 16.sp,
                        color = if (currentPlate == "common") MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { onClickLocal() }) {
                    Text(
                        "本地",
                        fontSize = 16.sp,
                        color = if (currentPlate == "local") MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
        HorizontalDivider(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}
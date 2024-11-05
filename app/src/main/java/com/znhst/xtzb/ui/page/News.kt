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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.znhst.xtzb.network.NewsItem

@Composable
fun News(outNavController: NavController) {
    val navController = rememberNavController()

    fun onClickCommon() {
        navController.navigate("common_news")
    }

    fun onClickLocal() {
        navController.navigate("local_news")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TopStickyHeader(onClickCommon = { onClickCommon() }, onClickLocal = { onClickLocal() })

        Spacer(modifier = Modifier.height(56.dp))

        NavHost(navController = navController, startDestination = "common_news", modifier = Modifier.padding(top = 56.dp)) {
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
fun TopStickyHeader(onClickCommon: () -> Unit, onClickLocal: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)) {
            Row() {
                TextButton(onClick = { onClickCommon() }) {
                    Text("公共", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = { onClickLocal() }) {
                    Text("本地", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
        HorizontalDivider(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter))
    }
}
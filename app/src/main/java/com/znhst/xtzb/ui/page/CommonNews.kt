package com.znhst.xtzb.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.znhst.xtzb.network.NewsItem
import com.znhst.xtzb.viewModel.NewsViewModel

val colors = listOf(
    Color(0xFF5F8C6B), // Announcement
    Color(0xFF6168A7), // News
    Color(0xFF7363A7), // Spotlight
    Color(0xFFA05A89), // Training
    Color(0xFFC2694E), // Files
    Color(0xFFB68A4D), // Topic
    Color(0xFF6C9975)  // Nutrition
)

@Composable
fun CommonNews(
    newsViewModel: NewsViewModel = viewModel(),
    navController: NavController,
    outNavController: NavController
) {
    val categories by newsViewModel.categoryList.collectAsState()

    var currentPage by remember { mutableStateOf(0) }
    var pageSize by remember { mutableStateOf(10) }
    val currentNewsList by newsViewModel.currentNewsList.collectAsState()
    val currentTotal by newsViewModel.currentTotal.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }

    newsViewModel.fetchCategories()
    LaunchedEffect(categories, selectedTabIndex) {
        if (categories.isNotEmpty()) {
            val selectedCategoryType = categories[selectedTabIndex].type
            newsViewModel.fetchNews(selectedCategoryType, currentPage, pageSize)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部的滚动 Tab 切换栏
        if (categories.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                edgePadding = 0.dp,
                indicator = {tabPositions ->
                    SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = colors[selectedTabIndex]
                    )
                }
            ) {
                categories.forEachIndexed { index, item ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            newsViewModel.fetchNews(
                                categories[selectedTabIndex].type,
                                currentPage,
                                pageSize
                            )
                        },
                        text = { Text(item.displayName, color = colors[index], fontSize = 16.sp) }
                    )
                }
            }
        } else {
            Text(
                text = "类别加载中...",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(currentNewsList) { newsItem ->
                NewsItemRow(newsItem, outNavController)
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun NewsItemRow(newsItem: NewsItem, outNavController: NavController) {

    fun onClickArticle() {
        val newsItemJson = Gson().toJson(newsItem)
        outNavController.navigate("article_viewer/${newsItemJson}")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onClickArticle()
            }
    ) {
        Text(
            text = newsItem.title,
            style = MaterialTheme.typography.headlineSmall, // Material 3 headline style
            color = MaterialTheme.colorScheme.onSurface // Adjusting color to match Material 3 color scheme
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "概述...",
            style = MaterialTheme.typography.bodyMedium, // Material 3 body style
            color = MaterialTheme.colorScheme.onSurfaceVariant // Variant for a less emphasized look
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = newsItem.updateTime,
            style = MaterialTheme.typography.bodySmall, // Smaller text for timestamps
            color = MaterialTheme.colorScheme.outline // Light gray for timestamps
        )
    }
}
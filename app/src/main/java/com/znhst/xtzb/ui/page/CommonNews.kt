package com.znhst.xtzb.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.znhst.xtzb.R
import com.znhst.xtzb.network.NewsItem
import com.znhst.xtzb.viewModel.NewsViewModel
import kotlinx.coroutines.launch

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
    val listState = rememberLazyListState()

    var selectedTabIndex by remember { mutableStateOf(0) }

    newsViewModel.fetchCategories()
    LaunchedEffect(categories) {
        if (categories.isNotEmpty()) {
            val selectedCategoryType = categories[selectedTabIndex].type
            newsViewModel.fetchNews(selectedCategoryType, currentPage, pageSize)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
            .collect { lastVisibleItem ->
                if (lastVisibleItem != null && lastVisibleItem.index == currentNewsList.size - 1) {
                    if (currentNewsList.size < currentTotal) {
                        currentPage += 1
                        newsViewModel.fetchNews(categories[selectedTabIndex].type, currentPage, pageSize)
                    }
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部的滚动 Tab 切换栏
        if (categories.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp)
                    .background(MaterialTheme.colorScheme.surface),
                edgePadding = 0.dp,
            ) {
                categories.forEachIndexed { index, item ->
                    val coroutineScope = rememberCoroutineScope()

                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            if(selectedTabIndex != index) {
                                selectedTabIndex = index
                                currentPage = 0

                                // 先滚动到顶部
                                coroutineScope.launch {
                                    listState.scrollToItem(0)
                                }

                                newsViewModel.fetchNews(
                                    categories[selectedTabIndex].type,
                                    currentPage,
                                    pageSize
                                )
                            }
                        },
                        modifier = Modifier
                            .padding(4.dp) // 添加一点内边距
                            .clip(RoundedCornerShape(5.dp)) // 圆角背景
                            .background(
                                if (selectedTabIndex == index) colors[index] else Color.Transparent
                            ), // 选中时背景为对应颜色
                        text = {
                            Text(
                                item.displayName,
                                color = if (selectedTabIndex == index) Color.White else Color.Gray,
                                fontWeight = if(selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }
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
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(currentNewsList) { newsItem ->
                NewsItemRow(newsItem, outNavController)
                Spacer(modifier = Modifier.height(6.dp))
            }

            // 加载更多提示
            if (currentNewsList.size < currentTotal) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClickArticle() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ArticlePreview(item = newsItem)
                Text(
                    text = newsItem.title,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val typeName = when (newsItem.kindName) {
                    "video" -> "视频"
                    "pdf" -> "PDF文档"
                    "word" -> "Word文档"
                    else -> "未知类型"
                }

                Text(
                    text = "类别: $typeName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = newsItem.updateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun ArticlePreview(item: NewsItem) {
    val icon = when (item.kindName) {
        "video" -> painterResource(id = R.drawable.article_mp4)
        "pdf" -> painterResource(id = R.drawable.article_pdf)
        "word" -> painterResource(id = R.drawable.article_doc)
        else -> painterResource(id = R.drawable.article_unknown)
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(48.dp) // 图标大小
        )
    }
}
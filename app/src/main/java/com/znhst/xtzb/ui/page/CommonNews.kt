package com.znhst.xtzb.ui.page

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    Color(0xFF5F8C6B),
    Color(0xFF6168A7),
    Color(0xFF7363A7),
    Color(0xFFA05A89),
    Color(0xFFC2694E),
    Color(0xFFB68A4D),
    Color(0xFF6C9975)
)

@Composable
fun CommonNews(
    newsViewModel: NewsViewModel = viewModel(),
    navController: NavController,
    outNavController: NavController
) {
    val categories by newsViewModel.categoryList.collectAsState()

    var currentPage by rememberSaveable { mutableStateOf(0) }
    var pageSize by rememberSaveable { mutableStateOf(10) }
    val currentNewsList by newsViewModel.currentNewsList.collectAsState()
    val currentTotal by newsViewModel.currentTotal.collectAsState()
    val listState = rememberLazyListState()

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        newsViewModel.fetchCategories()
    }
    LaunchedEffect(categories) {
        if (categories.isNotEmpty()) {
            if (currentNewsList.isEmpty()) {
                newsViewModel.clearNewsList()
                val selectedCategoryType = categories[selectedTabIndex].type
                newsViewModel.fetchNews(selectedCategoryType, currentPage, pageSize)
            }
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
        if (categories.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp),
                edgePadding = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                divider = {}
            ) {
                categories.forEachIndexed { index, item ->
                    val coroutineScope = rememberCoroutineScope()
                    val tabColor = colors[index % colors.size]

                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            if(selectedTabIndex != index) {
                                selectedTabIndex = index
                                currentPage = 0

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
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selectedTabIndex == index) tabColor else Color.Transparent
                            ),
                        text = {
                            Text(
                                item.displayName,
                                color = if (selectedTabIndex == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if(selectedTabIndex == index) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 14.sp
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
        ) {
            items(currentNewsList) { newsItem ->
                NewsItemRow(newsItem, outNavController)
            }

            if (currentNewsList.size < currentTotal) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
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
            .clickable { onClickArticle() },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArticlePreview(item = newsItem)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = newsItem.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
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
                        text = typeName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )

                    Text(
                        text = newsItem.updateTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

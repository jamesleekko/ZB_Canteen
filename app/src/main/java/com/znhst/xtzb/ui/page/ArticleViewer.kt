package com.znhst.xtzb.ui.page

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import com.znhst.xtzb.BuildConfig
import com.znhst.xtzb.network.NewsItem
import com.znhst.xtzb.viewModel.ArticleViewModel

@Composable
fun ArticleViewer(
    viewModel: ArticleViewModel = viewModel(),
    newsItem: NewsItem,
    outNavController: NavController
) {
    Column {
        when (newsItem.kindName) {
            "pdf" -> PdfViewer(newsItem.fileName!!, onExit = {
                outNavController.popBackStack()
            })
            "video" -> VideoViewer(newsItem.fileName!!, onExit = {
                outNavController.popBackStack()
            })
        }
    }
}

@Composable
fun PdfViewer(fileName: String, onExit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        val pdfUrl = "${BuildConfig.BASE_URL}/article/${fileName}"
        val pdfState = rememberVerticalPdfReaderState(
            resource = ResourceType.Remote(pdfUrl),
            isZoomEnable = true
        )

        // Pdf 组件垂直居中显示
        VerticalPDFReader(
            state = pdfState,
            modifier = Modifier
                .align(Alignment.Center) // 垂直居中
        )

        SmallFloatingActionButton(
            onClick = onExit,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.align(Alignment.TopStart).offset(x = 10.dp, y = 24.dp)
        ) {
            Icon(Icons.Filled.Close, "退出", tint = Color.White)
        }
    }
}

@Composable
fun VideoViewer(fileName: String, onExit: () -> Unit) {
    val videoUrl = "${BuildConfig.BASE_URL}/article/${fileName}"

    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()  // 准备播放
            playWhenReady = true // 自动播放
        }
    }

    // 在 UI 组件生命周期结束时释放 ExoPlayer
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 显示视频播放器
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )

        // 左上角的退出按钮
        IconButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(36.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "退出",
                tint = Color.Black
            )
        }
    }
}
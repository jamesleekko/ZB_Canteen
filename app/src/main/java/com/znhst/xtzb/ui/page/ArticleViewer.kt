package com.znhst.xtzb.ui.page

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.pratikk.jetpdfvue.state.VueFileType
import com.pratikk.jetpdfvue.state.VueLoadState
import com.pratikk.jetpdfvue.state.VueResourceType
import com.pratikk.jetpdfvue.state.rememberVerticalVueReaderState
import com.pratikk.jetpdfvue.util.compressImageToThreshold
import com.znhst.xtzb.BuildConfig
import com.znhst.xtzb.network.NewsItem
import com.znhst.xtzb.viewModel.ArticleViewModel
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        val pdfUrl = "${BuildConfig.BASE_URL}/article/${fileName}"

        val verticalVueReaderState = rememberVerticalVueReaderState(
            resource = VueResourceType.Remote(
                url = pdfUrl,
                fileType = VueFileType.PDF
            ),
            cache = 3 // By default 0
        )

        val launcher = verticalVueReaderState.getImportLauncher(interceptResult = {
            it.compressImageToThreshold(2)
        })

        val containerSize = IntSize(constraints.maxWidth, constraints.maxHeight)

        LaunchedEffect(Unit) {
            verticalVueReaderState.load(
                context,
                scope,
                containerSize,
                true,
                null
            )
        }

        when (verticalVueReaderState.vueLoadState) {
            is VueLoadState.NoDocument -> {
                Button(onClick = {
                    verticalVueReaderState.launchImportIntent(
                        context = context,
                        launcher = launcher
                    )
                }) {
                    Text(text = "Import Document")
                }
            }

            is VueLoadState.DocumentError -> {
                Column {
                    Text(text = "Error:  ${verticalVueReaderState.vueLoadState.getErrorMessage}")
                    Button(onClick = {
                        scope.launch {
                            verticalVueReaderState.load(
                                context = context,
                                coroutineScope = scope,
                                containerSize = containerSize,
                                isPortrait = true,
                                customResource = null
                            )
                        }
                    }) {
                        Text(text = "Retry")
                    }
                }
            }

            is VueLoadState.DocumentImporting -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("pdf加载中...", color = Color.White)
                }
            }

            is VueLoadState.DocumentLoading -> {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("pdf加载中...", color = Color.White)
                }
            }

            is VueLoadState.DocumentLoaded -> {
                VerticalPdfViewer(
                    modifier = Modifier.fillMaxHeight(),
                    onExit = onExit,
                    verticalVueReaderState = verticalVueReaderState,
                ) {
                    verticalVueReaderState.launchImportIntent(
                        context = context,
                        launcher = launcher
                    )
                }

            }
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
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "退出",
                tint = Color.White
            )
        }
    }
}
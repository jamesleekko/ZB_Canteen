package com.znhst.xtzb.ui.page

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val fileIdentifier = newsItem.filePath ?: newsItem.fileName ?: ""
    Column {
        when (newsItem.kindName) {
            "pdf" -> PdfViewer(fileIdentifier, onExit = {
                outNavController.popBackStack()
            })

            "video" -> VideoViewer(fileIdentifier, onExit = {
                outNavController.popBackStack()
            })
        }
    }
}

@Composable
fun PdfViewer(storedFileName: String, onExit: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        val encodedName = java.net.URLEncoder.encode(storedFileName, "UTF-8")
        val pdfUrl = "${BuildConfig.BASE_URL}/api/file/download?dir=article&name=$encodedName"

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
                DocumentErrorScreen(
                    message = "未找到文档",
                    onRetry = {
                        scope.launch {
                            verticalVueReaderState.load(
                                context = context,
                                coroutineScope = scope,
                                containerSize = containerSize,
                                isPortrait = true,
                                customResource = null
                            )
                        }
                    },
                    onBack = onExit
                )
            }

            is VueLoadState.DocumentError -> {
                DocumentErrorScreen(
                    message = verticalVueReaderState.vueLoadState.getErrorMessage
                        ?: "文档加载失败",
                    onRetry = {
                        scope.launch {
                            verticalVueReaderState.load(
                                context = context,
                                coroutineScope = scope,
                                containerSize = containerSize,
                                isPortrait = true,
                                customResource = null
                            )
                        }
                    },
                    onBack = onExit
                )
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
fun VideoViewer(storedFileName: String, onExit: () -> Unit) {
    val encodedName = java.net.URLEncoder.encode(storedFileName, "UTF-8")
    val videoUrl = "${BuildConfig.BASE_URL}/api/file/download?dir=article&name=$encodedName"

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
        SmallFloatingActionButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 32.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "退出",
                tint = Color.White
            )
        }
    }
}

@Composable
fun DocumentErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = Color.White.copy(alpha = 0.7f)
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "文档加载失败",
            color = Color.White,
            fontSize = 20.sp,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(text = "重试", fontSize = 16.sp)
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(46.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(text = "返回", fontSize = 16.sp)
        }
    }
}
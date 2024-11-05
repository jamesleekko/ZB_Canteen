package com.znhst.xtzb.ui.page

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rizzi.bouquet.ResourceType
import com.rizzi.bouquet.VerticalPDFReader
import com.rizzi.bouquet.rememberVerticalPdfReaderState
import com.znhst.xtzb.BuildConfig
import com.znhst.xtzb.network.NewsItem
import com.znhst.xtzb.viewModel.ArticleViewModel

@Composable
fun ArticleViewer(viewModel: ArticleViewModel = viewModel(), newsItem: NewsItem) {
    Column {

        Log.d("newsitem", newsItem.toString())
        when (newsItem.kindName) {
            "pdf" -> PdfViewer(newsItem.fileName!!)
        }
    }
}

@Composable
fun PdfViewer(fileName: String) {
    val pdfUrl = "${BuildConfig.BASE_URL}/article/${fileName}"
    val pdfState = rememberVerticalPdfReaderState(
        resource = ResourceType.Remote(pdfUrl),
        isZoomEnable = true
    )

    VerticalPDFReader(
        state = pdfState,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Gray)
    )
}

@Composable
fun VideoViewer(fileName: String) {

}
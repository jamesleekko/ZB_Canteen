package com.znhst.xtzb.viewModel

import android.app.Application
import android.util.Log
import android.util.Size
import androidx.compose.runtime.MutableState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.znhst.xtzb.dataModel.EZNewsCategory
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.network.NewsItem
import com.znhst.xtzb.network.NewsListRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(application: Application): AndroidViewModel(application) {
    private val _categoryList = MutableStateFlow<List<EZNewsCategory>>(emptyList())
    val categoryList: StateFlow<List<EZNewsCategory>> = _categoryList

    private val _currentNewsList = MutableStateFlow<List<NewsItem>>(emptyList())
    val currentNewsList: StateFlow<List<NewsItem>> = _currentNewsList

    private val _currentTotal = MutableStateFlow<Int?>(null)
    val currentTotal: StateFlow<Int?> = _currentTotal

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = ApiClient.apiService.getNewsCategories()
                Log.d("拉取到新闻类型列表:", result.toString())
                _categoryList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching news categories: ${e.message}"
            }
        }
    }

    fun fetchNews(type: Int?, page: Int? = 0, pageSize: Int? = 10) {
        viewModelScope.launch {
            try {
                val request = NewsListRequest(type = type, page = page, pageSize = pageSize)
                val result = ApiClient.apiService.getNewsList(request)
                _currentNewsList.value = result.list
                _currentTotal.value = result.total
                Log.d("拉取到新闻列表:", result.list.toString())
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching news list: ${e.message}"
                Log.e("Error fetching news list: ",e.message.toString())
            }
        }
    }
}
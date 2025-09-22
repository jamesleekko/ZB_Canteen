package com.znhst.xtzb.viewModel

import android.app.Application
import android.util.Log
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

    private val _currentTotal = MutableStateFlow<Int>(0)
    val currentTotal: StateFlow<Int> = _currentTotal

    private var lastFetchedType: Int? = null

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

    fun clearNewsList() {
        _currentNewsList.value = emptyList()
        _currentTotal.value = 0
        lastFetchedType = null
    }

    fun fetchNews(type: Int?, page: Int? = 0, pageSize: Int? = 10) {
        viewModelScope.launch {
            try {
                val request = NewsListRequest(type = type, page = page, pageSize = pageSize)
                val result = ApiClient.apiService.getNewsList(request)

                // 如果类型发生变化，则重置列表
                if (type != lastFetchedType) {
                    _currentNewsList.value = result.list
                    lastFetchedType = type // 更新上次请求的类型
                } else {
                    // 增量加载新闻列表
                    _currentNewsList.value += result.list
                }

                _currentTotal.value = result.total
                Log.d("加载到新闻列表:", result.list.toString())
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching news list: ${e.message}"
                Log.e("Error fetching news list: ",e.message.toString())
            }
        }
    }
}
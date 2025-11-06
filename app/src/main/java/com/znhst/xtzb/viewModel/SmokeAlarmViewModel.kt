package com.znhst.xtzb.viewModel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.network.SmokeAlarmEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class SmokeAlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val _historyList = MutableStateFlow<List<SmokeAlarmEntry>>(emptyList())
    val historyList: StateFlow<List<SmokeAlarmEntry>> = _historyList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchHistory(deviceNo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 获取当前时间
                val now = LocalDateTime.now()

                // 计算一周前的时间
                val oneMonthAgo = now.minus(1, ChronoUnit.MONTHS)

                // 定义时间格式化模式
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                // 格式化开始时间和结束时间
                val formattedStart = oneMonthAgo.format(formatter)
                val formattedEnd = now.format(formatter)
                val dayufengToken = ApiClient.apiService.getDayufengToken()
                val result = ApiClient.dayufengApiService.getSmokeAlarmHistory(
                    "Bearer $dayufengToken",
                    "2",
                    deviceNo,
                    "1",
                    "30",
                    formattedStart,
                    formattedEnd
                )
                Log.d("拉取到冰箱历史记录列表:", result.toString())
                _historyList.value = result.data!!.data.data
            } catch (e: Exception) {
                Log.d("Error fetching freezer history:", "${e.message}")
                _errorMessage.value = "Error fetching freezer history: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
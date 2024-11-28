package com.znhst.xtzb.viewModel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.network.TempHumiEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TempHumiViewModel(application: Application) : AndroidViewModel(application) {

    private val _historyList = MutableStateFlow<List<TempHumiEntry>>(emptyList())
    val historyList: StateFlow<List<TempHumiEntry>> = _historyList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchHistory(deviceNo: String) {
        viewModelScope.launch {
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
                val result = ApiClient.dayufengApiService.getTempHumiHistory(
                    "Bearer $dayufengToken",
                    "2",
                    deviceNo,
                    "1",
                    "30",
                    formattedStart,
                    formattedEnd
                )
                Log.d("拉取到温湿度一体机历史记录列表:", result.toString())
                _historyList.value = result.data!!.data.data
            } catch (e: Exception) {
                Log.d("Error fetching temp&humi history:", "${e.message}")
                _errorMessage.value = "Error fetching temp&humi history: ${e.message}"
            }
        }
    }
}
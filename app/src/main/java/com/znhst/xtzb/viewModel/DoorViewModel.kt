package com.znhst.xtzb.viewModel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.network.DoorOperationEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoorViewModel(application: Application) : AndroidViewModel(application) {

    private val _historyList = MutableStateFlow<List<DoorOperationEntry>>(emptyList())
    val historyList: StateFlow<List<DoorOperationEntry>> = _historyList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchHistory(doorGuid: String, page:Int = 1, limit:Int = 100, startTime: String?, endTime: String?) {
        viewModelScope.launch {
            try {
                val dayufengToken = ApiClient.apiService.getDayufengToken()
                val result = ApiClient.dayufengApiService.getDoorHistory(
                    "Bearer $dayufengToken",
                    2,
                    doorGuid,
                    page,
                    limit,
                    startTime,
                    endTime,
                )
                Log.d("拉取到门禁记录列表:", result.toString())
                _historyList.value = result.data!!.data
            } catch (e: Exception) {
                Log.d("Error fetching door history:", "${e.message}")
                _errorMessage.value = "Error fetching door history: ${e.message}"
            }
        }
    }
}
package com.znhst.xtzb.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.znhst.xtzb.dataModel.ZBDeviceCategory
import com.znhst.xtzb.dataModel.ZBDeviceInfo
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeviceViewModel(application: Application) : AndroidViewModel(application) {
    var tokenManager = TokenManager(getApplication<Application>().applicationContext)

    private val _categoryList = MutableStateFlow<List<ZBDeviceCategory>>(emptyList())
    val categoryList: StateFlow<List<ZBDeviceCategory>> = _categoryList

    private val _cameraList = MutableStateFlow<List<ZBDeviceInfo>>(emptyList())
    val cameraList: StateFlow<List<ZBDeviceInfo>> = _cameraList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val result = ApiClient.apiService.getDeviceCategories()
                Log.d("拉取到设备类型列表:", result.toString())
                _categoryList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching device categories: ${e.message}"
            }
        }
    }

    fun fetchCameras() {
        viewModelScope.launch {
            try {
                val result = ApiClient.apiService.getCameras()
                Log.d("拉取到摄像头列表:", result.toString())
                _cameraList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching devices: ${e.message}"
            }
        }
    }
}
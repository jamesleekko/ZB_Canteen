package com.znhst.xtzb.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.znhst.xtzb.dataModel.DoorInfo
import com.znhst.xtzb.dataModel.EZDeviceCategory
import com.znhst.xtzb.dataModel.EZDeviceInfo
import com.znhst.xtzb.dataModel.FreezerInfo
import com.znhst.xtzb.dataModel.SmokeAlarmInfo
import com.znhst.xtzb.dataModel.TempHumiInfo
import com.znhst.xtzb.dataModel.TempHumiCategory
import com.znhst.xtzb.network.ApiClient
import com.znhst.xtzb.utils.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeviceViewModel(application: Application) : AndroidViewModel(application) {
    var tokenManager = TokenManager(getApplication<Application>().applicationContext)

    private val _categoryList = MutableStateFlow<List<EZDeviceCategory>>(emptyList())
    val categoryList: StateFlow<List<EZDeviceCategory>> = _categoryList

    private val _cameraList = MutableStateFlow<List<EZDeviceInfo>>(emptyList())
    val cameraList: StateFlow<List<EZDeviceInfo>> = _cameraList

    private val _freezerList = MutableStateFlow<List<FreezerInfo>>(emptyList())
    val freezerList: StateFlow<List<FreezerInfo>> = _freezerList

    private val _smokeAlarmList = MutableStateFlow<List<SmokeAlarmInfo>>(emptyList())
    val smokeAlarmList: StateFlow<List<SmokeAlarmInfo>> = _smokeAlarmList

    private val _doorList = MutableStateFlow<List<DoorInfo>>(emptyList())
    val doorList: StateFlow<List<DoorInfo>> = _doorList

    private val _tempHumiList = MutableStateFlow<List<TempHumiInfo>>(emptyList())
    val tempHumiList: StateFlow<List<TempHumiInfo>> = _tempHumiList

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

    fun fetchDoors() {
        viewModelScope.launch {
            try {
                val result = ApiClient.apiService.getDayufengDoors()
                Log.d("拉取到门禁设备列表:", result.toString())
                _doorList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching doors: ${e.message}"
            }
        }
    }

    fun fetchFreezers() {
        viewModelScope.launch {
            try {
                val result = ApiClient.apiService.getDayufengFreezers()
                Log.d("拉取到冰箱列表:", result.toString())
                _freezerList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching freezers: ${e.message}"
            }
        }
    }

    fun fetchSmokeAlarms() {
        viewModelScope.launch {
            try {
                val result = ApiClient.apiService.getDayufengSmokeAlarms()
                Log.d("拉取到烟雾报警器列表:", result.toString())
                _smokeAlarmList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching devices: ${e.message}"
            }
        }
    }

    fun fetchTempHumis(category: TempHumiCategory) {
        viewModelScope.launch {
            try {
                val result = ApiClient.apiService.getTempHumis(category)
                Log.d("拉取到温度湿度传感器列表:", result.toString())
                _tempHumiList.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching temp&humis: ${e.message}"
            }
        }
    }
}
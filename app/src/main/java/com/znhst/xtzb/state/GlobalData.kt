package com.znhst.xtzb.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object GlobalData {
    // 定义全局的 MutableLiveData
    private val _ezAccessToken = MutableLiveData<String>()
    private val _ezExpireTime = MutableLiveData<Long>()
    val ezAccessToken: LiveData<String> = _ezAccessToken
    val ezExpireTime: LiveData<Long> = _ezExpireTime

    fun updateEZAccessToken(newData: String) {
        _ezAccessToken.value = newData
    }

    fun asyncUpdateEZAccessToken(newData: String) {
        _ezAccessToken.postValue(newData)
    }

    fun updateEZExpireTime(time: Long) {
        _ezExpireTime.value = time
    }

    fun asyncUpdateEZExpireTime(time: Long) {
        _ezExpireTime.postValue((time))
    }
}
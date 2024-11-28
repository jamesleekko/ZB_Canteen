package com.znhst.xtzb.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.znhst.xtzb.dataModel.Stock
import com.znhst.xtzb.dataModel.StockInboundRecord
import com.znhst.xtzb.dataModel.StockItem
import com.znhst.xtzb.dataModel.StockOutboundRecord
import com.znhst.xtzb.network.ApiClient
import kotlinx.coroutines.launch

class StockViewModel(application: Application) : AndroidViewModel(application){

    private val _stockItems = MutableLiveData<List<StockItem>>()
    val stockItems: LiveData<List<StockItem>> = _stockItems

    private val _stocks = MutableLiveData<List<Stock>>()
    val stocks: LiveData<List<Stock>> = _stocks

    fun loadStockItems() {
        viewModelScope.launch {
            try{
                _stockItems.value = ApiClient.apiService.getStockItems()
            } catch( e: Error) {

            }
        }
    }

    fun loadStocks() {
        viewModelScope.launch {
            try{
                _stocks.value = ApiClient.apiService.getStocks()
            } catch (e: Error) {

            }
        }
    }

    fun addInboundRecord(record: StockInboundRecord) {
        viewModelScope.launch {
            ApiClient.apiService.addInboundRecord(record)
        }
    }

    fun addOutboundRecord(record: StockOutboundRecord) {
        viewModelScope.launch {
            ApiClient.apiService.addOutboundRecord(record)
        }
    }
}
package com.phivantuan.sigmatest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class MainViewModel : ViewModel() {

    val status = MutableLiveData<Int>().apply { value = 0 }
    private val _lstData =
        MutableLiveData<MutableList<String>>().apply { value = ArrayList() }

    val lstData: LiveData<MutableList<String>> = _lstData
    fun updateData(data: String) {
        _lstData.value?.add(data)
        _lstData.notifyObserver()
    }

    fun refreshData(){
        _lstData.postValue(mutableListOf())
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}
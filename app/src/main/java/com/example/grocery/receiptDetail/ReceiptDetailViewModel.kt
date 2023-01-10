package com.example.grocery.receiptDetail


import androidx.lifecycle.ViewModel
import com.example.grocery.DataRepository

class ReceiptDetailViewModel() : ViewModel() {

    fun receiptLiveData() = DataRepository.get()
}
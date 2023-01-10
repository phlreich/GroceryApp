package com.example.grocery.receiptList

import androidx.lifecycle.ViewModel
import com.example.grocery.DataRepository

class ReceiptListViewModel : ViewModel() {

    private val dataRepository = DataRepository.get()
    val receiptListLiveData = dataRepository.getReceipts()

}


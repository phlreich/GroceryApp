package com.example.grocery.receiptList

import androidx.lifecycle.ViewModel
import com.example.grocery.ReceiptRepository
import com.example.grocery.data.Receipt

class ReceiptListViewModel : ViewModel() {

    private val receiptRepository = ReceiptRepository.get()
    val receiptListLiveData = receiptRepository.getReceipts()

}


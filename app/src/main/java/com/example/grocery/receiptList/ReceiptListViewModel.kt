package com.example.grocery.receiptList

import androidx.lifecycle.ViewModel
import com.example.grocery.ReceiptRepository
import com.example.grocery.data.Receipt

class ReceiptListViewModel : ViewModel() {

    private val receiptRepository = ReceiptRepository.get()
    val receiptListLiveData = receiptRepository.getReceipts()

    init {
        receiptRepository.addReceipt(Receipt(title = "test title", items = mutableListOf("joghurt", "brogurt"), prices = mutableListOf(0.13.toFloat(), 4.5.toFloat())))
    }

}


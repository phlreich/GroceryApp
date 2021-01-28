package com.example.grocery

import androidx.lifecycle.ViewModel
import com.example.grocery.data.Receipt

class ReceiptListViewModel : ViewModel() {

    val receipts = mutableListOf<Receipt>()

    init {
        for (i in 0 until 100) {
            val receipt = Receipt()
            receipt.title = "Receipt #$i"
            receipts += receipt
        }
    }
}
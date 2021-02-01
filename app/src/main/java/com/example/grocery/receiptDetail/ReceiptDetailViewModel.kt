package com.example.grocery.receiptDetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.grocery.ReceiptRepository
import java.util.*

class ReceiptDetailViewModel() : ViewModel() {

    fun receiptLiveData() = ReceiptRepository.get()
}
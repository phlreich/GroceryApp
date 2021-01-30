package com.example.grocery

import android.app.Application

class GroceryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ReceiptRepository.initialize(this)
    }
}
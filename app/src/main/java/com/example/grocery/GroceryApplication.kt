package com.example.grocery

import android.app.Application


class GroceryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DataRepository.initialize(this)

    }
}
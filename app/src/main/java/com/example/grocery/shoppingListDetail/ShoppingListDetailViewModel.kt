package com.example.grocery.shoppingListDetail

import androidx.lifecycle.ViewModel
import com.example.grocery.DataRepository

class ShoppingListDetailViewModel() : ViewModel() {

    fun shoppingListLiveData() = DataRepository.get()
}
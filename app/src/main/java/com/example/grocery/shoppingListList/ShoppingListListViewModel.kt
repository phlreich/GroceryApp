package com.example.grocery.shoppingListList

import androidx.lifecycle.ViewModel
import com.example.grocery.DataRepository

class ShoppingListListViewModel : ViewModel() {

    private val dataRepository = DataRepository.get()
    val shoppingListListLiveData = dataRepository.getShoppingLists()

}
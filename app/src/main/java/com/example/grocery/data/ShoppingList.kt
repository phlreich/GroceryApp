package com.example.grocery.data

import java.util.*

data class ShoppingList (val id: UUID = UUID.randomUUID(),
                         var date: Date = Date())
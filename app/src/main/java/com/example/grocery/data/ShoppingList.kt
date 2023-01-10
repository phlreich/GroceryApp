package com.example.grocery.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class ShoppingList (@PrimaryKey val id: UUID = UUID.randomUUID(),
                         var date: Date = Date(),
                         var title: String = "",
                         var content: String = ""
)
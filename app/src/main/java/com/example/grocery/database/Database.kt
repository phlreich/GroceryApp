package com.example.grocery.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.grocery.data.Receipt
import com.example.grocery.data.ShoppingList
import com.example.grocery.database.receipt.ReceiptDao
import com.example.grocery.database.receipt.ReceiptTypeConverters
import com.example.grocery.database.shoppingList.ShoppingListDao
import com.example.grocery.database.shoppingList.ShoppingListTypeConverters

@Database(entities = [ Receipt::class, ShoppingList::class ], version=1)
@TypeConverters(ReceiptTypeConverters::class)
abstract class Database : RoomDatabase() {

    abstract fun receiptDao(): ReceiptDao

    abstract fun shoppingListDao(): ShoppingListDao

}

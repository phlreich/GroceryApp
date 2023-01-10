package com.example.grocery

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.grocery.data.Receipt
import com.example.grocery.data.ShoppingList
import com.example.grocery.database.Database
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "database"

class DataRepository private constructor(context: Context){

    private val database : Database = Room.databaseBuilder(
        context.applicationContext,
        Database::class.java,
        DATABASE_NAME
    ).build()

    private val receiptDao = database.receiptDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getReceipts(): LiveData<List<Receipt>> = receiptDao.getReceipts()

    fun getReceipt(id: UUID): LiveData<Receipt?> = receiptDao.getReceipt(id)

    fun addReceipt(receipt: Receipt) {
        executor.execute {
            receiptDao.addReceipt(receipt)
        }
    }

    fun deleteReceipt(receipt: Receipt) {
        executor.execute {
            receiptDao.deleteReceipt(receipt)
        }
    }

    fun deleteShoppingList(shoppingList: ShoppingList) {
        executor.execute {
            shoppingListDao.deleteShoppingList(shoppingList)
        }
    }

    private val shoppingListDao = database.shoppingListDao()

    fun getShoppingLists(): LiveData<List<ShoppingList>> = shoppingListDao.getShoppingLists()

    fun getShoppingList(id: UUID): LiveData<ShoppingList?> = shoppingListDao.getShoppingList(id)

    fun addShoppingList(shoppingList: ShoppingList) {
        executor.execute {
            shoppingListDao.addShoppingList(shoppingList)
        }
    }

    fun updateShoppingList(shoppingList: ShoppingList) {
        executor.execute {
        shoppingListDao.updateShoppingList(shoppingList)
        }
    }

    fun deleteAllShoppingLists() = shoppingListDao.deleteAllShoppingLists()

    fun deleteAllReceipts() = receiptDao.deleteAllReceipts()


    companion object {

        private var INSTANCE: DataRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = DataRepository(context)
            }
        }

        fun get(): DataRepository {
            return INSTANCE ?:
            throw IllegalStateException("ReceiptRepository must be initialized")
        }
    }
}
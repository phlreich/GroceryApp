package com.example.grocery.database.shoppingList

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.grocery.data.ShoppingList
import java.util.*

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shoppingList")
    fun getShoppingLists(): LiveData<List<ShoppingList>>

    @Query("SELECT * FROM shoppingList WHERE id=(:id)")
    fun getShoppingList(id: UUID): LiveData<ShoppingList?>

    @Insert
    fun addShoppingList(shoppingList: ShoppingList)

    @Update
    fun updateShoppingList(shoppingList: ShoppingList)

    @Delete
    fun deleteShoppingList(shoppingList: ShoppingList)

    @Query("DELETE FROM shoppingList")
    fun deleteAllShoppingLists()
}
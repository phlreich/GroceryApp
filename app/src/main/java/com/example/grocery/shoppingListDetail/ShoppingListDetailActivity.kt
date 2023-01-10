package com.example.grocery.shoppingListDetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.DataRepository
import com.example.grocery.R
import com.example.grocery.data.ShoppingList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class ShoppingListDetailActivity : AppCompatActivity() {

    private val dataRepository = DataRepository.get()

    private val shoppingListDetailViewModel: ShoppingListDetailViewModel by viewModels()
    private lateinit var currentShoppingList: ShoppingList



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_detail)

        val actionBar = supportActionBar
        actionBar!!.title = "Shopping List"
        actionBar.setDisplayHomeAsUpEnabled(true)

        val editTitle: EditText = findViewById(R.id.editTitleText)
        val editContent: EditText = findViewById(R.id.editTextTextMultiLine)
        val saveEdit: FloatingActionButton = findViewById(R.id.saveFloatingButton)
        val recommend: FloatingActionButton = findViewById(R.id.recommendationButton)

        val shoppingListId = UUID.fromString(intent.getStringExtra("shopping list id"))

        currentShoppingList = ShoppingList()

        val shoppingList = shoppingListDetailViewModel.shoppingListLiveData().getShoppingList(shoppingListId)
        val receipts = DataRepository.get().getReceipts()

        shoppingList.observe(this) {
            it?.let { shoppingList ->
                editTitle.setText(shoppingList.title)
                editContent.setText(shoppingList.content)
                currentShoppingList = shoppingList
            }
        }

//        var items = listOf("").toMutableList()
//        var counts = listOf(1).toMutableList()
        var combined = listOf(Pair("",1)).toMutableList()
        receipts.observe(this) {
            it?.let { receipts ->
                for (el in receipts) {
                    for (item in el.items) {
                        val items = combined.map { k -> k.first }
                        if (item in items) {
                            val ind = items.indexOf(item)
                            combined[ind] = Pair(combined[ind].first, combined[ind].second + 1)
                        } else {
                            combined.add(Pair(item, 1))
                        }
                    }
                }
            }
        }

        saveEdit.setOnClickListener {
            currentShoppingList.content = editContent.text.toString()
            currentShoppingList.title = editTitle.text.toString()
            dataRepository.updateShoppingList(currentShoppingList)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }

        recommend.setOnClickListener {
            var newSuggestion = editContent.text.toString()
            newSuggestion += "\n"
            combined.sortBy { it.second }
            combined.dropLastWhile { it.first in editContent.text.toString().split("\n") }
            Log.d("WHAT", editContent.text.toString().split("\n").toString())
            Log.d("WHAT", combined.toString())
            if (combined.size != 0) {
                val value = combined.last().first
                newSuggestion += value + ""//combined.maxByOrNull { it.second }!!.first
                if (combined.size > 1) combined = combined.subList(1, combined.lastIndex)
                editContent.setText(newSuggestion)
            }
        }
    }
}
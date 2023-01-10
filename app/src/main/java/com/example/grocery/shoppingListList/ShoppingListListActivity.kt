package com.example.grocery.shoppingListList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.DataRepository
import com.example.grocery.R
import com.example.grocery.data.ShoppingList
import com.example.grocery.shoppingListDetail.ShoppingListDetailActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

const val SHOPPING_LIST_ID = "shopping list id"

class ShoppingListListActivity : AppCompatActivity() {

    private val dataRepository = DataRepository.get()

    private val shoppingListListViewModel : ShoppingListListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_list)

        val actionBar = supportActionBar
        actionBar!!.title = "Shopping List"
        actionBar.setDisplayHomeAsUpEnabled(true)

        val addShoppingList: FloatingActionButton = findViewById(R.id.addShoppingList)

        val shoppingListRecyclerView: RecyclerView = findViewById(R.id.shopping_list_recycler_view)
        val shoppingListAdapter = ShoppingListAdapter { shoppingList -> adapterOnClick(shoppingList)  }

        var num = 0

        shoppingListRecyclerView.adapter = shoppingListAdapter

        shoppingListListViewModel.shoppingListListLiveData.observe(this) {
            it.let {
                shoppingListAdapter.submitList(it as MutableList<ShoppingList>)
                num = it.size + 1
            }
        }

        addShoppingList.setOnClickListener{
            val intent = Intent(this, ShoppingListDetailActivity::class.java)
            val id: UUID = UUID.randomUUID()
            val titleStart = "Shopping List #"

            dataRepository.addShoppingList(ShoppingList(id = id, title = titleStart + num.toString(), content = ""))
            intent.putExtra(SHOPPING_LIST_ID, id.toString())
            startActivity(intent)
        }
    }

    private fun adapterOnClick(shoppingList: ShoppingList) {
        val intent = Intent(this, ShoppingListDetailActivity::class.java)
        val id = shoppingList.id
        intent.putExtra(SHOPPING_LIST_ID, id.toString())
        startActivity(intent)
    }
}

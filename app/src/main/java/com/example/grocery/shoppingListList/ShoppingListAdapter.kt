package com.example.grocery.shoppingListList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.DataRepository
import com.example.grocery.R
import com.example.grocery.data.ShoppingList

class ShoppingListAdapter(private val onClick: (ShoppingList) -> Unit) :
        ListAdapter<ShoppingList, ShoppingListAdapter.ShoppingListViewHolder>(ShoppingListDiffCallback) {

    class ShoppingListViewHolder(itemView: View, val onClick: (ShoppingList) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.shopping_list_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.shopping_list_date)
        private val delCheckBox2: CheckBox = itemView.findViewById(R.id.checkBox2)
        private var currentShoppingList: ShoppingList? = null

        init {
            itemView.setOnClickListener {
                currentShoppingList?.let {
                    onClick(it)
                }
            }
        }
        fun bind(shoppingList: ShoppingList) {
            currentShoppingList = shoppingList

            titleTextView.text = shoppingList.title
            dateTextView.text = shoppingList.date.toString()
            delCheckBox2.setOnClickListener {
                DataRepository.get().deleteShoppingList(currentShoppingList!!)
            }
            delCheckBox2.isChecked = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_shopping_list, parent, false)

        return ShoppingListViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val shoppingList = getItem(position)
        holder.bind(shoppingList)
    }
}

object ShoppingListDiffCallback : DiffUtil.ItemCallback<ShoppingList>() {
    override fun areItemsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ShoppingList, newItem: ShoppingList): Boolean {
        return oldItem.id == newItem.id
    }
}
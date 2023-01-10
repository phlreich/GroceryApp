package com.example.grocery.receiptDetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R

class ReceiptDetailAdapter() : //private val itemList: List<Pair<String, Float>>
        ListAdapter<Pair<String, Float>, ReceiptDetailAdapter.ReceiptDetailViewHolder>(ItemDiffCallback) {

    class ReceiptDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        private val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        private var currentItem: Pair<String, Float>? = null


        fun bind(item: Pair<String, Float>) {

            currentItem = item

            itemName.text = item.first
            itemPrice.text = item.second.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.receipt_item, parent, false)

        return ReceiptDetailViewHolder(view)
    }
    override fun onBindViewHolder(holder: ReceiptDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object ItemDiffCallback : DiffUtil.ItemCallback<Pair<String, Float>>() {

    override fun areItemsTheSame(oldItem: Pair<String, Float>, newItem: Pair<String, Float>): Boolean {
        return (oldItem.first == newItem.first) && (oldItem.second == newItem.second)
    }

    override fun areContentsTheSame(oldItem: Pair<String, Float>, newItem: Pair<String, Float>): Boolean {
        return (oldItem.first == newItem.first) && (oldItem.second == newItem.second)
    }
}
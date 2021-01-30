package com.example.grocery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.data.Receipt
import java.util.*

class ReceiptAdapter() :
        ListAdapter<Receipt, ReceiptAdapter.ReceiptViewHolder>(ReceiptDiffCallback) {

    // Describes an item view and its place within the RecyclerView
    class ReceiptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.receipt_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.receipt_date)

        fun bind(receipt: Receipt) {
            titleTextView.text = receipt.title
            dateTextView.text = receipt.date.toString()
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_receipt, parent, false)

        return ReceiptViewHolder(view)
    }

    // Returns size of data list
//    override fun getItemCount(): Int {
//        return receipts.size
//    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receipt = getItem(position)
        holder.bind(receipt)
    //holder.bind(receipts[position].title, receipts[position].date)
    }
}

object ReceiptDiffCallback : DiffUtil.ItemCallback<Receipt>() {
    override fun areItemsTheSame(oldItem: Receipt, newItem: Receipt): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Receipt, newItem: Receipt): Boolean {
        return oldItem.id == newItem.id
    }
}

//class ReceiptAdapter(private var receipts: List<Receipt>) :
//        RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder>() {
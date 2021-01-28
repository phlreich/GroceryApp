package com.example.grocery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.data.Receipt
import java.util.*

class ReceiptAdapter(private var receipts: List<Receipt>) :
    RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class ReceiptViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.receipt_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.receipt_date)

        fun bind(title: String, date: Date) {
            titleTextView.text = title
            dateTextView.text = date.toString()
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_receipt, parent, false)

        return ReceiptViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return receipts.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        holder.bind(receipts[position].title, receipts[position].date)
    }
}


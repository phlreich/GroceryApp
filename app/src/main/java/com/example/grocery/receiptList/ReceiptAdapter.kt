package com.example.grocery.receiptList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R
import com.example.grocery.data.Receipt

class ReceiptAdapter(private val onClick: (Receipt) -> Unit) :
        ListAdapter<Receipt, ReceiptAdapter.ReceiptViewHolder>(ReceiptDiffCallback) {

    // Describes an item view and its place within the RecyclerView
    class ReceiptViewHolder(itemView: View, val onClick: (Receipt) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.item_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.receipt_date)
        private var currentReceipt: Receipt? = null

        init {
            itemView.setOnClickListener {
                currentReceipt?.let {
                    onClick(it)
                }
            }
        }
        fun bind(receipt: Receipt) {
            currentReceipt = receipt

            titleTextView.text = receipt.title
            dateTextView.text = receipt.date.toString()
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_receipt, parent, false)

        return ReceiptViewHolder(view, onClick)
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
package com.example.grocery.receiptList

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
import com.example.grocery.data.Receipt

class ReceiptAdapter(private val onClick: (Receipt) -> Unit) :
        ListAdapter<Receipt, ReceiptAdapter.ReceiptViewHolder>(ReceiptDiffCallback) {

    class ReceiptViewHolder(itemView: View, val onClick: (Receipt) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.item_name)
        private val dateTextView: TextView = itemView.findViewById(R.id.receipt_date)
        private val delCheckBox: CheckBox = itemView.findViewById(R.id.checkBox)
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
            delCheckBox.setOnClickListener {
                DataRepository.get().deleteReceipt(currentReceipt!!)

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_receipt, parent, false)

        return ReceiptViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receipt = getItem(position)
        holder.bind(receipt)

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
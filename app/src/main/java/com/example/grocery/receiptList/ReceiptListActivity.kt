package com.example.grocery.receiptList

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.grocery.R
import com.example.grocery.data.Receipt
import com.example.grocery.receiptDetail.ReceiptDetailActivity

const val RECEIPT_ID = "receipt id"

class ReceiptListActivity : AppCompatActivity() {


    private val receiptListViewModel: ReceiptListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_list)


        val receiptRecyclerView: RecyclerView = findViewById(R.id.receipt_recycler_view)
        val receiptAdapter = ReceiptAdapter { receipt -> adapterOnClick(receipt) }

        var receipts = emptyList<Receipt>()

        receiptRecyclerView.adapter = receiptAdapter

        receiptListViewModel.receiptListLiveData.observe(this, {
            it?.let {
                receiptAdapter.submitList(it as MutableList<Receipt>)
            }
        })

    }

    private fun adapterOnClick(receipt: Receipt) {
        val intent = Intent(this, ReceiptDetailActivity::class.java)
        intent.putExtra(RECEIPT_ID, receipt.id)
        startActivity(intent)
    }

}
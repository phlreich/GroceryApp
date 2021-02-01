package com.example.grocery.receiptDetail


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery.R
import com.example.grocery.data.Receipt
import java.util.*

const val RECEIPT_ID = "receipt id"

class ReceiptDetailActivity : AppCompatActivity() {

    private var itemList = mutableListOf<Pair<String, Float>>()
    private val receiptDetailViewModel: ReceiptDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_detail_view)

        val itemAdapter = ReceiptDetailAdapter()
        val itemRecyclerView: RecyclerView = findViewById(R.id.item_recycler_view_detail)

        itemRecyclerView.adapter = itemAdapter

        val receiptId = intent.getStringExtra(RECEIPT_ID)
        val receipt = receiptDetailViewModel.receiptLiveData().getReceipt(UUID.fromString(receiptId))

        receipt.observe(this,
        androidx.lifecycle.Observer {
            it?.let { receipt ->
                val itemNames = receipt.items
                val itemPrices = receipt.prices
                itemList = itemNames.zip(itemPrices) as MutableList<Pair<String, Float>>
                itemAdapter.submitList(itemList)
            }
        })
    }

    private fun getContents(receipt: Receipt){

    }

    private fun adapterOnClick(receipt: Pair<String, Float>) {
//        val intent = Intent(this, someActivity::class.java)
//        intent.putExtra()
//        startActivity(intent)
    }
}
package com.example.grocery

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.grocery.data.Receipt


class ReceiptListActivity : AppCompatActivity() {


    private val receiptListViewModel: ReceiptListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_list)


        val receiptRecyclerView: RecyclerView = findViewById(R.id.receipt_recycler_view)
        val receiptAdapter = ReceiptAdapter()

        var receipts = emptyList<Receipt>()

        receiptRecyclerView.adapter = receiptAdapter

        receiptListViewModel.receiptListLiveData.observe(this, {
            it?.let {
                receiptAdapter.submitList(it as MutableList<Receipt>)
            }
        })

    }

}
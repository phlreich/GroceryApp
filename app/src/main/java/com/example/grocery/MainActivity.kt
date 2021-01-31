package com.example.grocery


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.camera.core.ImageCapture


class MainActivity : AppCompatActivity() {

    private lateinit var receiptsButton: Button
    private lateinit var scanButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        receiptsButton = findViewById(R.id.receipts_button)
        scanButton = findViewById(R.id.scan_button)

        receiptsButton.setOnClickListener{
            val intent = Intent(this, ReceiptListActivity::class.java)
            startActivity(intent)
        }

        scanButton.setOnClickListener{

        }
    }


}
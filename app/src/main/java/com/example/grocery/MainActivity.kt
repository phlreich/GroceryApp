package com.example.grocery



import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.grocery.data.Receipt
import com.example.grocery.receiptList.ReceiptListActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition


private const val TAG = "OCRStatus"
class MainActivity : AppCompatActivity() {

    private val receiptRepository = ReceiptRepository.get()

    private lateinit var receiptsButton: Button
    private lateinit var scanReceiptButton: Button

    private var imageUri: Uri? = null

    private var image: InputImage? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        receiptsButton = findViewById(R.id.receipts_button)
        scanReceiptButton = findViewById(R.id.scan_button)

        if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    111
            )
        } else scanReceiptButton.isEnabled = true

        receiptsButton.setOnClickListener{
            val intent = Intent(this, ReceiptListActivity::class.java)
            startActivity(intent)
        }

        scanReceiptButton.setOnClickListener{
            selectOption()
        }
    }

    private fun selectOption() {
        val options = arrayOf<CharSequence>("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Import from...")

        builder.setNegativeButton("Cancel")
        { dialog, id ->
            // User cancelled the dialog
        }
        builder.setItems(options) { _, item ->
            if (options[item] == "Camera") {
                val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(camera, 0)




            } else if (options[item] == "Gallery") {
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(gallery, 1)
            }
        }
        builder.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            image = InputImage.fromBitmap(data?.getParcelableExtra<Bitmap>("data")!!, 90)
            ocr()
        }
        if(requestCode == 1 && resultCode == RESULT_OK && data != null){
            // Image is imported from the gallery
            imageUri = data.data
            image = InputImage.fromFilePath(applicationContext, imageUri!!)
            ocr()
            }
        }

    private fun ocr() {
        val recognizer = TextRecognition.getClient()
        recognizer.process(image!!)
                .addOnSuccessListener {
                    textToReceipt(it.text)
                    Log.d("RESULT", it.text)
                }
    }

    private fun textToReceipt(input: String) {
        var listAll = input.split('\n')
        var prices = listAll
                .asSequence()
                .filter{Regex("^[0-9 ]+,[0-9]{2} (A|B)").containsMatchIn(it)}
                .map { it.trim() }
                .map { it.takeWhile { char -> !char.isWhitespace() } }
                .map { it.replace(',','.') }
                .map { it.toFloat() }
                .toList()
        var items = listAll
                .asSequence()
                .filter{!Regex("^[0-9 ]+,[0-9]{2} (A|B)").containsMatchIn(it)}
                .filter{!Regex("EUR").containsMatchIn(it)}
                .filter{!Regex(" x|x ").containsMatchIn(it)}
                .filter{!Regex("MwSt.").containsMatchIn(it)}
                .filter{Regex("[a-z]").containsMatchIn(it)}
                .map { it.trim() }
                .toList()
        items = items.subList(4, prices.size + 4)

        receiptRepository.addReceipt(Receipt(title = items[0],
                items = items.toMutableList(),
                prices = prices.toMutableList()))
        }

}




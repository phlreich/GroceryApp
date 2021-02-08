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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.grocery.data.Receipt
import com.example.grocery.receiptList.ReceiptListActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition


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

                }
    }

    private fun textToReceipt(input: String) {
        var listAll = input.split('\n')
        var prices = listAll
                .asSequence()
                .filter{Regex("^[0-9 ]+,[ ]*[0-9]{2} ([AB]|[12])").containsMatchIn(it)}

                .map { Regex("[a-zA-Z ]").replace(it, "") }
                .map { it.replace(',','.') }
                .map { it.toFloat() }
                .toList()
        var items = listAll
                .asSequence()
                .filter{!Regex("^[0-9 ]+,[ ]*[0-9]{2} ([AB]|[12])").containsMatchIn(it)}
                .filter{!Regex(" x|x ").containsMatchIn(it)}
                .filter{!Regex("Stk").containsMatchIn(it)}
                .filter{!Regex("MwSt.").containsMatchIn(it)} //BAD AD-HOC STUFF
                .filter{Regex("[a-zA-Z]").containsMatchIn(it)}
                .map { it.trim() }
                .map { str -> str.dropWhile { it.isDigit() } }
                .map { it.trim() }
                .toList()

        var indexEUR = items.indexOf("EUR")
        var indexUIDnr = items.indexOfFirst { Regex("UID Nr").containsMatchIn(it) }
        if (indexUIDnr == -1) indexUIDnr = 9000
        var startIndex = minOf(indexEUR, indexUIDnr)
        items = if (startIndex == indexUIDnr) {
            items.subList(startIndex + 1, startIndex + 2 + prices.size)
        } else {
            items.subList(startIndex + 1, startIndex + 1 + prices.size)
        }

        items = items.filterNot { (it == "EUR") }

//        var rawPrices = listAll.filter{Regex("^[0-9 ]+,[ ]*[0-9]{2} ([AB]|[12])").containsMatchIn(it)}
//        var rawItems = listAll.filter{!Regex("^[0-9 ]+,[ ]*[0-9]{2} ([AB]|[12])").containsMatchIn(it)}
//        Log.d("OCR_DATA", listAll.toString())
//        Log.d("OCR_DATA", rawItems.toString())
//        Log.d("OCR_DATA", rawPrices.toString())
//        Log.d("OCR_DATA", items.toString())
//        Log.d("OCR_DATA", prices.toString())
        receiptRepository.addReceipt(Receipt(title = items[0],
                items = items.toMutableList(),
                prices = prices.toMutableList())
        )
        }
}




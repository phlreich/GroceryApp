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
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import kotlin.math.abs


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
//                    textToReceipt(it.text)
                    textToReceiptUsingHeight(it)
                }
    }

    private fun textToReceipt(input: String) {
        val listAll = input.split('\n')
        val prices = listAll
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
                .filter { it.length > 2 }
                .toList()

        val indexEUR = items.indexOf("EUR")
        var indexUIDnr = items.indexOfFirst { Regex("UID Nr").containsMatchIn(it) }
        if (indexUIDnr == -1) indexUIDnr = 9000
        val startIndex = minOf(indexEUR, indexUIDnr)
        items = if (startIndex == indexUIDnr) {
            items.subList(startIndex + 1, startIndex + 2 + prices.size)
        } else {
            items.subList(startIndex + 1, startIndex + 1 + prices.size)
        }

        items = items.filterNot { (it == "EUR") }

        val rawPrices = listAll.filter{Regex("^[0-9 ]+,[ ]*[0-9]{2} ([AB]|[12])").containsMatchIn(it)}
        val rawItems = listAll.filter{!Regex("^[0-9 ]+,[ ]*[0-9]{2} ([AB]|[12])").containsMatchIn(it)}
        Log.d("OCR_DATA", listAll.toString())
        Log.d("OCR_DATA", rawItems.toString())
        Log.d("OCR_DATA", rawPrices.toString())
        Log.d("OCR_DATA", items.toString())
        Log.d("OCR_DATA", prices.toString())
        receiptRepository.addReceipt(Receipt(title = items[0],
                items = items.toMutableList(),
                prices = prices.toMutableList())
        )
        }

    private fun textToReceiptUsingHeight(input: Text) {

        val prices = emptyList<List<Double>>().toMutableList() //list of line-data in form [blocknumber, linenumber, height, price]
        val items = emptyList<List<Double>>().toMutableList() //list of line-data in form [blocknumber, linenumber, height]
        var blockCounter = 0
        for (block in input.textBlocks) {

            var lineCounter = 0
            for (line in block.lines) {

                var y = 0.0
                line.cornerPoints?.forEach { y += it.y }
                y /= 4


                if (Regex("^[0-9 ]+,[ ]*[0-9]{2} ([AB]|[12])").containsMatchIn(line.text)) {

                    prices.add(listOf(blockCounter.toDouble(),
                            lineCounter.toDouble(),
                            y,
                            Regex("[a-zA-Z ]").replace(line.text, "")
                                    .replace(',','.')
                                    .toDouble()))
                } else {
                    items.add(listOf(blockCounter.toDouble(),
                            lineCounter.toDouble(),
                            y))
                }
                lineCounter += 1
            }
            blockCounter += 1
        }

        val formattedItems = emptyList<String>().toMutableList()

        for (price in prices) {

            var closest = emptyList<Int>()
            var min = Double.MAX_VALUE

            for (item in items) {
                val diff = abs(item[2]-price[2])
                if (min > diff && (price[0] != item[0] || price[1] != item[1])) {
                    min = diff
                    closest = listOf(item[0].toInt(), item[1].toInt())
                }
            }

            formattedItems.add(input.textBlocks[closest[0]].lines[closest[1]].text
                    .trim()
                    .dropWhile { it.isDigit() }
                    .trim())

        }
        val pricesFinal = emptyList<Float>().toMutableList()

        for (p in prices) {
            pricesFinal.add(p[3].toFloat())
        }

        receiptRepository.addReceipt(Receipt(title = formattedItems[0],
                items = formattedItems.toMutableList(),
                prices = pricesFinal)
        )

    }
}

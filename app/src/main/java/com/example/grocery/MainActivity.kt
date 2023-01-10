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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.grocery.data.Receipt
import com.example.grocery.ml.Grocery
import com.example.grocery.receiptDetail.ReceiptDetailActivity
import com.example.grocery.receiptList.ReceiptListActivity
import com.example.grocery.shoppingListList.ShoppingListListActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private val dataRepository = DataRepository.get()

    private lateinit var scanReceiptButton: Button
    private lateinit var shoppingListButton: Button
    private lateinit var receiptsButton: Button
    private lateinit var profileButton: Button


    private var image: InputImage? = null
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.title = "Grocery++"
        scanReceiptButton = findViewById(R.id.scan_button)
        receiptsButton = findViewById(R.id.receipts_button)
        shoppingListButton = findViewById(R.id.shopping_lists)
        profileButton = findViewById(R.id.profileButton)

        // Take picture button functionality --------------------------------------------------------------------------
        scanReceiptButton.isEnabled = true

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
        } else
            scanReceiptButton.isEnabled = true


        scanReceiptButton.setOnClickListener {
            selectOption()
        }
//
        shoppingListButton.setOnClickListener {
            val intent = Intent(this, ShoppingListListActivity::class.java)
            startActivity(intent)
        }

        receiptsButton.setOnClickListener{
            val intent = Intent(this, ReceiptListActivity::class.java)
            startActivity(intent)
        }

        profileButton.setOnClickListener{
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        var inputs = (1..4).map { (1..15).random() }
//        inputs = inputs.map { Random().nextInt() }
        val deb = tfmodel(inputs).toList().toString()
        Log.d("TFLITE", inputs.toString())
        Log.d("TFLITE", deb)
        // --------------------------------------------------------------------

    } // End of onCreate

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
                try {
                    textToReceiptUsingHeight(it)
                } catch (e: Exception) {
                    Log.e("OCR_FAILURE", "ocr failed with error: ", e)
                    Toast.makeText(this, "Scan receipt failed", Toast.LENGTH_SHORT).show()
                }
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
            .filter{!Regex("MwSt.").containsMatchIn(it)}
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
        dataRepository.addReceipt(
            Receipt(title = items[0],
            items = items.toMutableList(),
            prices = prices.toMutableList())
        )
    }

    private fun textToReceiptUsingHeight(input: Text) {

        val isPriceFormat = Regex("^[0-9 ]+,[ ]*[0-9]{2} ([AB]|[12])")

        val prices = emptyList<List<Double>>().toMutableList() //list of line-data in form [blocknumber, linenumber, height, price]
        val items = emptyList<List<Double>>().toMutableList() //list of line-data in form [blocknumber, linenumber, height]
        var blockCounter = 0
        for (block in input.textBlocks) {

            var lineCounter = 0
            for (line in block.lines) {

                var y = 0.0
                line.cornerPoints?.forEach { y += it.y }
                y /= 4

                if (isPriceFormat.containsMatchIn(line.text)) {

                    var price = Regex("[a-zA-Z *]").replace(line.text, "")
                        .replace(',','.')

                    if (Regex(",[0-9]{3}").containsMatchIn(line.text)) {
                        price = price.substring(0, price.length - 1)
                    }

                    prices.add(listOf(blockCounter.toDouble(),
                        lineCounter.toDouble(),
                        y,
                        price.toDouble()))
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
        items.sortBy { it[2] }

        for (price in prices) {

            var closest= emptyList<Double>()

            var min = Double.MAX_VALUE

            for (item in items) {
                val diff = abs(item[2]-price[2])
                if (min > diff && (price[0] != item[0] || price[1] != item[1])) {
                    min = diff
                    closest = item
                }
            }

            var closestText = input.textBlocks[closest[0].toInt()].lines[closest[1].toInt()].text

            while (Regex("( x|x )|(EUR/)").containsMatchIn(closestText)) { //correcting for REWE formatting
                closest = items[items.indexOf(closest) - 1]
                closestText = input.textBlocks[closest[0].toInt()].lines[closest[1].toInt()].text
            }

            formattedItems.add(closestText
                .trim()
                .dropWhile { it.isDigit() }
                .trim())
        }
        val pricesFinal = emptyList<Float>().toMutableList()

        for (p in prices) {
            pricesFinal.add(p[3].toFloat())
        }

        Log.d("OCR_DATA", input.text)
        Log.d("OCR_DATA", formattedItems.toString())
        Log.d("OCR_DATA", prices.toString())
        val newID = UUID.randomUUID()
        dataRepository.addReceipt(
            Receipt(id = newID,
            title = formattedItems[0],
            items = formattedItems.toMutableList(),
            prices = pricesFinal)
        )

        val intent = Intent(this, ReceiptDetailActivity::class.java)
        intent.putExtra("receipt id", newID.toString())
        startActivity(intent)
    }

    fun tfmodel(list: List<Int>): FloatArray {

        val model = Grocery.newInstance(this)

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 4), DataType.FLOAT32)
        val byteBuffer: ByteBuffer = ByteBuffer.allocate(16)
        byteBuffer.asFloatBuffer()
        for (ind in (0..3)) {
            byteBuffer.putFloat(list[ind].toFloat())
        }
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Releases model resources if no longer used.
        model.close()

        return outputFeature0.floatArray

    }
}
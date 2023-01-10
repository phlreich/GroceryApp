package com.example.grocery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.observe
import com.example.grocery.data.Receipt
import com.example.grocery.data.ShoppingList
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit

class Profile : AppCompatActivity() {

    private lateinit var textViewEmail: TextView
    private lateinit var textViewName: TextView
    private lateinit var textViewID: TextView
    private lateinit var backupButton: Button
    private lateinit var deletBackupButton: Button

    private val dataRepository = DataRepository.get()

    var receipts = listOf<Receipt>()
    var shoppingLists = listOf<ShoppingList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dataRepository.getReceipts().observe(this) {
            receipts = it
        }
        dataRepository.getShoppingLists().observe(this) {
            shoppingLists = it
        }

        val actionBar = supportActionBar
        actionBar!!.title = "Profile"
        actionBar.setDisplayHomeAsUpEnabled(true)


        textViewEmail = findViewById(R.id.textViewEmail)
        textViewName = findViewById(R.id.textViewName)
        textViewID = findViewById(R.id.textViewID)
        backupButton = findViewById(R.id.backupButton)
        deletBackupButton = findViewById(R.id.deleteBackupButton)

        // get Token
        val pref = getSharedPreferences("token", Context.MODE_PRIVATE);
        // def vlaue is need when not data was found
        val token = pref.getString("access_token", "Not found!").toString()

        sendJson(token)

        // get id for backups
        val pref2 = getSharedPreferences("id", Context.MODE_PRIVATE);

        // def vlaue is need when not data was found
        val id = pref2.getString("id", "Not found!").toString()

        backupButton.setOnClickListener {
            val data = getBackup()
            sendBackup(token, id, data)
        }

        deletBackupButton.setOnClickListener {
            delete(token, id)
        }
    }

    fun sendJson(token: String){

        // Create Retrofit
        val retrofit = Retrofit.Builder()
                .baseUrl("https://grocerypp.herokuapp.com")
                .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.profile("Bearer " + token)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().create()
                    val prettyJson = gson.toJson(
                            JsonParser.parseString(
                                    response.body()
                                            ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                            )
                    )

                    var gson2 = Gson()
                    var data = JSONObject(prettyJson)

                    val email = data["email"].toString()
                    val name = data["name"].toString()
                    val id = data["id"].toString()

                    textViewEmail.text = email
                    textViewName.text = name
                    textViewID.text = id

                } else {

                    Toast.makeText(applicationContext, response.code().toString(), Toast.LENGTH_SHORT).show()

                }
            }
        }


    }


    fun sendBackup(token: String, id: String, data: String){

        // Create Retrofit
        val retrofit = Retrofit.Builder()
                .baseUrl("https://grocerypp.herokuapp.com")
                .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("data", data)
        jsonObject.put("owner_id", id)



        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        // Create RequestBody ( We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody )
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.backup(requestBody,"Bearer " + token)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().create()
                    val prettyJson = gson.toJson(
                            JsonParser.parseString(
                                    response.body()
                                            ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                            )
                    )

                    //var gson2 = Gson()
                    //var data = JSONObject(prettyJson)
                    //val id = data["data"].toString()
                    Toast.makeText(applicationContext, "Backup successful!", Toast.LENGTH_SHORT).show()

                } else {

                    Toast.makeText(applicationContext, "Error: " + response.code().toString(), Toast.LENGTH_SHORT).show()

                }
            }
        }


    }


    fun delete(token: String, id: String){

        // Create Retrofit
        val retrofit = Retrofit.Builder()
                .baseUrl("https://grocerypp.herokuapp.com/backups/" + id + "/")
                .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)


        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.delete("Bearer " + token)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    Toast.makeText(applicationContext, "Backups deleted successfully!", Toast.LENGTH_SHORT).show()

                } else {

                    Toast.makeText(applicationContext, "Error: " + response.code().toString(), Toast.LENGTH_SHORT).show()

                }
            }
        }


    }


    private fun getBackup(): String {
        var res = ""
        res += shoppingLists.toString()
        res += ";;;"
        res += receipts.toString()
        //Log.d("BACKUP", res)
        return res
    }

    private fun restoreFromBackup(backupString: String) {
        dataRepository.deleteAllReceipts()
        dataRepository.deleteAllShoppingLists()
        val shoppingLists = backupString.split(";;;")[0]
        val receipts = backupString.split(";;;")[1]
    }
}

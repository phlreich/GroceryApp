package com.example.grocery

//import `META-INF` android.content.Intent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import java.io.File

class RegisterActivity : AppCompatActivity() {


    private lateinit var textRegisterUsername: EditText
    private lateinit var textRegisterMail: EditText
    private lateinit var textRegisterPassword: EditText
    private lateinit var buttonRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val actionBar = supportActionBar
        actionBar!!.title = "Register"
        actionBar.setDisplayHomeAsUpEnabled(true)

        // init
        textRegisterUsername = findViewById(R.id.textLoginUsername)
        textRegisterMail = findViewById(R.id.textRegisterMail)
        textRegisterPassword = findViewById(R.id.textLoginPassword)
        buttonRegister = findViewById(R.id.buttonRegister)

        buttonRegister.setOnClickListener {

            // get Inputs
            var name = textRegisterUsername.text.toString()
            var email = textRegisterMail.text.toString()
            var password = textRegisterPassword.text.toString()

            if(name.isEmpty()){
                Toast.makeText(applicationContext, "Username required!",Toast.LENGTH_SHORT).show()
            } else {
                if (email.isEmpty()){
                    Toast.makeText(applicationContext, "E-Mail required!",Toast.LENGTH_SHORT).show()
                } else {
                    if (password.isEmpty()){
                        Toast.makeText(applicationContext, "Password required!",Toast.LENGTH_SHORT).show()
                    } else {
                        sendJSON(name, email, password)
                    }
                }
            }
        }
    }


    fun sendJSON(name: String, email: String, password: String) {

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://grocerypp.herokuapp.com")
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)

        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("name", name)
        jsonObject.put("password", password)

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        // Create RequestBody (We're not using any converter, like GsonConverter, MoshiConverter e.t.c, that's why we use RequestBody)
        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            // Do the POST request and get response
            val response = service.register(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    // Convert raw JSON to pretty JSON using GSON library
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string() // About this thread blocking annotation : https://github.com/square/retrofit/issues/3255
                        )
                    )

                    var gson2 = Gson()
                    var data = JSONObject(prettyJson)
                    val id = data["id"].toString()

                    // save id in sharedPreferences
                    val pref = getApplicationContext().getSharedPreferences("id", MODE_PRIVATE)

                    val editor = pref.edit()

                    // save id
                    editor.putString("id", id)
                    editor.commit()

                    Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_SHORT).show()

                    // start Login Activity
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)

                } else {
                    if(response.code() == 409){
                        Toast.makeText(applicationContext, "E-Mail already registered!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Validation Error!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
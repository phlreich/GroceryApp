package com.example.grocery

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

class LoginActivity : AppCompatActivity() {

    private lateinit var loginRegister: TextView
    private lateinit var loginButton: Button
    private lateinit var textLoginUsername: EditText
    private lateinit var textLoginPassword: EditText
    private lateinit var skipButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login3)

        supportActionBar?.hide()

        loginRegister = findViewById(R.id.loginRegister)
        loginButton = findViewById(R.id.buttonLogin)

        skipButton = findViewById(R.id.skipbutton)

        textLoginUsername = findViewById(R.id.textLoginUsername)
        textLoginPassword = findViewById(R.id.textLoginPassword)

        val content = SpannableString("New? Register")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        loginRegister.setText(content)


        loginButton.setOnClickListener {


            val username = textLoginUsername.text.toString()
            val password = textLoginPassword.text.toString()

            if(username.isEmpty()){
                Toast.makeText(applicationContext, "Username required!",Toast.LENGTH_SHORT).show()
            } else {
                if (password.isEmpty()){
                    Toast.makeText(applicationContext, "Password required!",Toast.LENGTH_SHORT).show()
                } else {
                    formData(username, password)
                }
            }

        }

        skipButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        loginRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun formData(username: String, password: String) {

        // Create Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://grocerypp.herokuapp.com")
            .build()

        // Create Service
        val service = retrofit.create(APIService::class.java)


        val fields: HashMap<String?, RequestBody?> = HashMap()
        fields["username"] = (username).toRequestBody("text/plain".toMediaTypeOrNull())
        fields["password"] = (password).toRequestBody("text/plain".toMediaTypeOrNull())


        CoroutineScope(Dispatchers.IO).launch {

            // Do the POST request and get response
            val response = service.login(fields)

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
                    val token = data["access_token"].toString()


                    // save token
                    val pref = getApplicationContext().getSharedPreferences("token", MODE_PRIVATE)

                    val editor = pref.edit()

                    // save token
                    editor.putString("access_token", token)
                    editor.commit()


                    Toast.makeText(applicationContext, "Welcome to Grocery++!", Toast.LENGTH_SHORT).show()

                    // display Token
                    //Toast.makeText(applicationContext, token, Toast.LENGTH_SHORT).show()

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)

                } else {

                    if(response.code() == 401){
                        Toast.makeText(applicationContext, "Incorrect email or password!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Validation Error!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

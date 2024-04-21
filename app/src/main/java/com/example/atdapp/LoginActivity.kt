package com.example.atdapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {

            val email = findViewById<EditText>(R.id.emailLogin).text.toString()
            val password = findViewById<EditText>(R.id.passwordLogin).text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(applicationContext, "valeur invalide", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val jsonParams = JSONObject()
            jsonParams.put("email", email)
            jsonParams.put("password", password)


            val queue = Volley.newRequestQueue(this)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, "${Constant.API_BASE_URL}/logIn", jsonParams,
                { response ->

                    val token = response.getString("token")

                    val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("auth_token", token)
                    editor.apply()

                    val i = Intent(this,HomeActivity::class.java)
                    startActivity(i)
                },
                { error ->
                    if(error.networkResponse.statusCode == 422) {
                        Toast.makeText(applicationContext,R.string.loginError,Toast.LENGTH_LONG).show()
                    }
                }
            )
            queue.add(jsonObjectRequest)
        }
    }
}
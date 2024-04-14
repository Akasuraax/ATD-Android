package com.example.atdapp

import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
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

            val i = Intent(this,HomeActivity::class.java)
            startActivity(i)

            val queue = Volley.newRequestQueue(this)
            val request = StringRequest(Request.Method.GET,
                "${Constant.API_BASE_URL}/login",
                { content ->
                val global = JSONObject(content)
                    Toast.makeText(applicationContext,global.toString(),Toast.LENGTH_LONG).show()
                },
                { error ->
                    Toast.makeText(applicationContext,error.message.toString(),Toast.LENGTH_LONG).show()
                }
            )
            queue.add(request)
        }
    }
}
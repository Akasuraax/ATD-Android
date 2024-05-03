package com.example.atdapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

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
                    val userJson = JSONObject(response.getString("user"))
                    val userId = userJson.getString("id")

                    val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("auth_token", token)
                    editor.putString("userId", userId)
                    editor.putLong("lastLogin", System.currentTimeMillis())


                    editor.apply()

                    val i = Intent(this,HomeActivity::class.java)
                    startActivity(i)
                },
                { error ->
                    if (error.networkResponse!= null) {
                        val statusCode = error.networkResponse.statusCode
                        if (statusCode == 422 || statusCode == 401) {
                            showFailureLoginDialog()
                        }
                    } else {
                        showFailureRequestDialog()
                    }
                }
            )
            queue.add(jsonObjectRequest)
        }
    }

    private fun showFailureLoginDialog() {
        val builder = AlertDialog.Builder(this)
        // Inflétez la vue de votre AlertDialog ici
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.failure_dialog, null)
        builder.setView(dialogView)

        val failureDone = dialogView.findViewById<Button>(R.id.failureDone)
        dialogView.findViewById<TextView>(R.id.failureDesc).text = getString(R.string.failureLogin)
        failureDone.text = getString(R.string.failureLoginDone)
        dialogView.findViewById<TextView>(R.id.failureTitle).text = getString(R.string.failureLoginTitle)


        val alertDialog = builder.create()

        failureDone.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }

    private fun showFailureRequestDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.failure_dialog, null)
        builder.setView(dialogView)

        val failureDone = dialogView.findViewById<Button>(R.id.failureDone)
        dialogView.findViewById<TextView>(R.id.failureDesc).text = getString(R.string.failureRequest)
        failureDone.text = getString(R.string.failureLoginDone)
        dialogView.findViewById<TextView>(R.id.failureTitle).text = getString(R.string.failureLoginTitle)


        val alertDialog = builder.create()

        failureDone.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertDialog.show()
    }

}
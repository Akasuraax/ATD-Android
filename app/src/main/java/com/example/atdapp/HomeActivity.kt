package com.example.atdapp

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private lateinit var pendingIntent: PendingIntent
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        replaceFragment(HomeFragment())
        var menu = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        menu.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(ProfilFragment())
                R.id.setting -> replaceFragment(SettingFragment())
                R.id.scanner -> replaceFragment(ScannerFragment())
                R.id.nfc -> replaceFragment(NFCFragment())

                else -> {}
            }
            true
        }
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onResume() {
        super.onResume()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("CHKI", "NEW NFC DETECTED")

        if (intent?.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                try {
                    ndef.connect()
                    val ndefMessage = ndef.ndefMessage
                    if (ndefMessage != null) {
                        var data =""

                        val record = ndefMessage.records[0]

                            val payload = record.payload
                            data = String(payload, Charsets.UTF_8)

                            val knownHeader = "en"

                            if (data.startsWith(knownHeader)) {
                                data = data.substring(knownHeader.length)
                            }
                        Toast.makeText(this,data,Toast.LENGTH_LONG).show()

                        val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
                        val authToken = sharedPreferences.getString("auth_token", "")
                        val userId = sharedPreferences.getString("userId", "")

                        val params = JSONObject()
                        params.put("id_volunteer", userId)
                        params.put("id_beneficiary", data)
                        Log.d("token",authToken.toString())
                        val queue = Volley.newRequestQueue(this)
                        val jsonObjectRequest = object : JsonObjectRequest(
                            Method.POST, "${Constant.API_BASE_URL}/visit",
                            params,
                            { response ->
                                Toast.makeText(this, "Merci pour votre visite ", Toast.LENGTH_LONG).show()
                            },
                            { error ->
                                Toast.makeText(this, error.networkResponse.statusCode.toString(), Toast.LENGTH_LONG).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Authorization"] = "$authToken"
                                return headers
                            }
                        }

                        queue.add(jsonObjectRequest)


                    }
                } catch (e: IOException) {
                    Log.e("NFC", "Error reading NFC tag", e)
                } finally {
                    try {
                        ndef.close()
                    } catch (e: IOException) {
                        Log.e("NFC", "Error closing NFC", e)
                    }
                }
            }
        }
    }
}
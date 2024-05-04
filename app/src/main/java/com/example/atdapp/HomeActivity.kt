package com.example.atdapp

import SettingsAdapter
import SettingsFragment
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HomeActivity"
        private const val NFC_ACTION = NfcAdapter.ACTION_NDEF_DISCOVERED
        private const val AUTH_TOKEN_KEY = "auth_token"
        private const val USER_ID_KEY = "userId"
    }

    private lateinit var pendingIntent: PendingIntent
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        setupWindowInsets()
        replaceFragment(HomeFragment())

        val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val nightMode = sharedPreferences.getInt("nightMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(nightMode)

        setupNfc()
        setupBottomNavigation()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.home
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNfc() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    private fun setupBottomNavigation() {
        val menu = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        menu.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.profile -> replaceFragment(VisitFragment())
                R.id.setting -> replaceFragment(SettingsFragment())
                R.id.scanner -> replaceFragment(ScannerFragment())
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
        setupNfcForegroundDispatch()
    }

    private fun setupNfcForegroundDispatch() {
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
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent?.action == NFC_ACTION) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                try {
                    processNdefMessage(ndef)
                } catch (e: IOException) {
                    Log.e(TAG, "Error reading NFC tag", e)
                } finally {
                    try {
                        ndef.close()
                    } catch (e: IOException) {
                        Log.e(TAG, "Error closing NFC", e)
                    }
                }
            }
        }
    }

    private fun processNdefMessage(ndef: Ndef) {
        try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            if (ndefMessage != null) {
                val data = extractDataFromNdefMessage(ndefMessage)
                sendDataToServer(data)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error processing NFC message", e)
        }
    }

    private fun extractDataFromNdefMessage(ndefMessage: NdefMessage): String {
        val record = ndefMessage.records[0]
        val payload = record.payload
        var data = String(payload, Charsets.UTF_8)
        val knownHeader = "en"
        if (data.startsWith(knownHeader)) {
            data = data.substring(knownHeader.length)
        }
        return data
    }

    private fun sendDataToServer(data: String) {
        val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString(AUTH_TOKEN_KEY, "")
        val userId = sharedPreferences.getString(USER_ID_KEY, "")
        val params = JSONObject()
        params.put("id_volunteer", userId)
        params.put("id_beneficiary", data)

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.POST, "${Constant.API_BASE_URL}/visit",
            params,
            { response ->
                showSuccessDialog()
            },
            { error ->
                if (error.networkResponse!= null) {
                val statusCode = error.networkResponse.statusCode
                if (statusCode == 422) {
                    showFailureVisitDialog()
                }
                } else {
                    showFailureRequestDialog()
                }
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "$authToken"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

    private fun showFailureRequestDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
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

    private fun showFailureVisitDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.failure_dialog, null)
        builder.setView(dialogView)
        val failureDone = dialogView.findViewById<Button>(R.id.failureDone)
        dialogView.findViewById<TextView>(R.id.failureDesc).text = getString(R.string.failureVisitDesc)
        failureDone.text = getString(R.string.failureLoginDone)
        dialogView.findViewById<TextView>(R.id.failureTitle).text = getString(R.string.error)
        val alertDialog = builder.create()
        failureDone.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun showSuccessDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.sucess_dialog, null)
        builder.setView(dialogView)
        val failureDone = dialogView.findViewById<Button>(R.id.successDone)
        dialogView.findViewById<TextView>(R.id.successDesc).text = getString(R.string.successVisit)
        failureDone.text = getString(R.string.close)
        dialogView.findViewById<TextView>(R.id.successTitle).text =
            getString(R.string.successTitleVisit)
        val alertDialog = builder.create()
        failureDone.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }
}

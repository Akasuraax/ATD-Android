package com.example.atdapp

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class WriteNfcActivity : AppCompatActivity() {

    private var mNfcAdapter: NfcAdapter? = null
    private lateinit var nfcUtil: NFCUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_write_nfcactivity)
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcUtil = NFCUtil()

    }

    override fun onResume() {
        super.onResume()
        mNfcAdapter?.let {
            nfcUtil.enableNFCInForeground(it, this, javaClass)
        }
    }

    override fun onPause() {
        super.onPause()
        mNfcAdapter?.let {
            nfcUtil.disableNFCInForeground(it,this)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val messageWrittenSuccessfully = nfcUtil.createNFCMessage("56", intent)
        Toast.makeText(this,ifElse(messageWrittenSuccessfully,"Successful Written to Tag","Something When wrong Try Again"),
            Toast.LENGTH_LONG).show()
    }

    fun<T> ifElse(condition: Boolean, primaryResult: T, secondaryResult: T) = if (condition) primaryResult else secondaryResult
}
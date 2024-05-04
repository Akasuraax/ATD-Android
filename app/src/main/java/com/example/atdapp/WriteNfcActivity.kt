package com.example.atdapp

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.IOException

class WriteNfcActivity : AppCompatActivity() {

    private var id:Int ? = 0
    companion object {
        private const val TAG = "WriteNfcActivity"
        private const val NFC_ACTION = NfcAdapter.ACTION_TAG_DISCOVERED
    }

    private lateinit var pendingIntent: PendingIntent
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_nfcactivity)
        setupNfc()

        val name = intent.extras?.getString("name", "")
        val forname = intent.extras?.getString("forname", "")

        findViewById<TextView>(R.id.tv_name_nfc).text = name + " " + forname


        this.id = intent.extras?.getInt("id", 0)
    }

    private fun setupNfc() {
        val intent = Intent(this, WriteNfcActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
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
        Log.d("TAG", "${intent?.action}")
        if (intent?.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            Log.d("TAG", "Tag discovered.")
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag!= null) {
                writeDataToNfcTag(tag)
            }
        }
    }

    private fun writeDataToNfcTag(tag: Tag) {
        val ndef = Ndef.get(tag)
        if (ndef!= null) {
            try {
                ndef.connect()
                val mimeType = "application/vnd.eautantdone.eautantdone"
                if(this.id != null) {
                    val payload = this.id.toString().toByteArray(Charsets.UTF_8)
                    val nfcRecord = NdefRecord(
                        NdefRecord.TNF_MIME_MEDIA,
                        mimeType.toByteArray(Charsets.UTF_8),
                        null,
                        payload
                    )
                    val nfcMessage = NdefMessage(arrayOf(nfcRecord))
                    ndef.writeNdefMessage(nfcMessage)
                    Log.d(TAG, "Data written to NFC tag successfully.")
                    showSuccessWriteNfcDialog()
                    findViewById<ImageView>(R.id.iv_nfc).setColorFilter(
                        ContextCompat.getColor(
                            this,
                            R.color.green
                        ), PorterDuff.Mode.SRC_IN
                    )
                } else {
                    showFailureWriteNfcDialog()
                }
            } catch (e: IOException) {
                showFailureWriteNfcDialog()
                Log.e(TAG, "Error writing to NFC tag", e)
            } finally {
                try {
                    ndef.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Error closing NFC", e)
                }
            }
        }
    }

    private fun showFailureWriteNfcDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.failure_dialog, null)
        builder.setView(dialogView)
        val failureDone = dialogView.findViewById<Button>(R.id.failureDone)
        dialogView.findViewById<TextView>(R.id.failureDesc).text = getString(R.string.badWrite)
        failureDone.text = getString(R.string.failureLoginDone)
        dialogView.findViewById<TextView>(R.id.failureTitle).text = getString(R.string.error)
        val alertDialog = builder.create()
        failureDone.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    private fun showSuccessWriteNfcDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.sucess_dialog, null)
        builder.setView(dialogView)
        val failureDone = dialogView.findViewById<Button>(R.id.successDone)
        dialogView.findViewById<TextView>(R.id.successDesc).text = getString(R.string.goodWrite)
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
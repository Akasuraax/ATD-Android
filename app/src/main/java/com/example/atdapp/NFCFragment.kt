package com.example.atdapp

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * A simple [Fragment] subclass.
 * Use the [NFCFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NFCFragment : Fragment() {

    companion object {
        private const val TAG = "NFCFragment"
        private const val NFC_NOT_SUPPORTED_MESSAGE = "NFC non pris en charge sur cet appareil."
        private const val NFC_SUPPORTED_MESSAGE = "NFC pris en charge sur cet appareil. Configuration NFC en cours..."
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_n_f_c, container, false)

        val nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        if (nfcAdapter == null) {
            showToastAndLog(NFC_NOT_SUPPORTED_MESSAGE)
        } else {
            setupNfc(nfcAdapter)
        }

        return view
    }

    private fun setupNfc(nfcAdapter: NfcAdapter) {
        val pendingIntent = createPendingIntent()
        val intentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val filters = arrayOf(intentFilter)

        nfcAdapter.enableForegroundDispatch(requireActivity(), pendingIntent, filters, null)
        Log.d(TAG, NFC_SUPPORTED_MESSAGE)
    }

    private fun createPendingIntent(): PendingIntent {
        return PendingIntent.getActivity(requireContext(), 0,
            Intent(requireContext(), javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE)
    }

    private fun showToastAndLog(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        Log.d(TAG, message)
    }

    override fun onPause() {
        super.onPause()
        val nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext())
        nfcAdapter?.disableForegroundDispatch(requireActivity())
    }
}

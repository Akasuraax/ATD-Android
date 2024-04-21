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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_n_f_c, container, false)

        val adapter = NfcAdapter.getDefaultAdapter(requireContext())
        if (adapter == null) {

            Toast.makeText(requireContext(), "NFC non pris en charge sur cet appareil.", Toast.LENGTH_SHORT).show()
            Log.d("NFCFragment", "NFC non pris en charge sur cet appareil.")

        } else {

            val pendingIntent = PendingIntent.getActivity(requireContext(), 0,
                Intent(requireContext(), javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_IMMUTABLE)
            val intentFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            val filters = arrayOf(intentFilter)

            adapter.enableForegroundDispatch(requireActivity(), pendingIntent, filters, null)
            Log.d("NFCFragment", "NFC pris en charge sur cet appareil. Configuration NFC en cours...")

        }

        return view
    }

    override fun onPause() {
        super.onPause()
        val adapter = NfcAdapter.getDefaultAdapter(requireContext())
        adapter?.disableForegroundDispatch(requireActivity())
    }
}

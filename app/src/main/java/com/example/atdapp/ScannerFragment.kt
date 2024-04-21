package com.example.atdapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.atdapp.databinding.FragmentScannerBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import org.json.JSONException
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [ScannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScannerFragment : Fragment() {
    private lateinit var binding: FragmentScannerBinding
    private lateinit var qrScanIntegrator: IntentIntegrator
    private lateinit var global: JSONObject

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScannerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupScanner()

        val scanButton = binding.fab
            scanButton.setOnClickListener {
                initiateScan()
            }

        binding.newScanButton.setOnClickListener {
            initiateScan()
        }

        binding.suppButton.setOnClickListener {
            if (::global.isInitialized && global.has("delete_url")) {
                val deleteUrl = global.getString("delete_url")
                val queue = Volley.newRequestQueue(this.context)
                val jsonObjectRequest = StringRequest(
                    Request.Method.DELETE, deleteUrl,
                    { response ->
                        global = JSONObject(response)
                        Toast.makeText(this.context,"lot supprimer",Toast.LENGTH_LONG).show()

                        binding.textResult.text = "Lot supprimer"
                        binding.suppButton.visibility = View.GONE
                        binding.newScanButton.visibility = View.GONE
                        binding.fab.visibility = View.VISIBLE
                        global = JSONObject()
                    },
                    { error ->
                        Toast.makeText(this.context,error.networkResponse.statusCode.toString(),Toast.LENGTH_LONG).show()
                    }
                )
                queue.add(jsonObjectRequest)
            }
        }
    }

    private fun initiateScan() {
        qrScanIntegrator.initiateScan()
    }
    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator.forSupportFragment(this)
        qrScanIntegrator.setOrientationLocked(false)
        qrScanIntegrator.setPrompt("Scannez le code en mode portrait")
    }


    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "pas trouver", Toast.LENGTH_LONG).show()
            } else {
                try {
                    global = JSONObject(result.contents)
                    binding.textResult.text = "${global.getString("name")} ${global.getString("count")} ${global.getString("measure")} \n expire le : ${global.getString("expired_date")}"
                    binding.suppButton.visibility = View.VISIBLE
                    binding.newScanButton.visibility = View.VISIBLE
                    binding.fab.visibility = View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(activity, "erreur : ${result.contents}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
package com.example.atdapp

import android.R
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.atdapp.adapter.ActivityListAdapter
import com.example.atdapp.databinding.ActivityDetailsBinding
import com.example.atdapp.model.ActivityList
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ActivityDetails : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.visibility = View.GONE


        val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", "")
        val userId = sharedPreferences.getString("userId", "")


        val id:Int ? = intent.extras?.getInt("id", 0)

        var queue = Volley.newRequestQueue(this)
        val requete: StringRequest = object : StringRequest(
            Request.Method.GET,
            Constant.API_BASE_URL + "/activity/publicActivity/${id}?user=${userId}",
            { content ->
                var res = JSONObject(content)
                var activity = res.getJSONObject("activity")
                binding.title.text = activity.getString("title")
                binding.address.text = activity.getString("address")
                binding.paragraph.text = activity.getString("description")
                binding.dateRange.text = "du " + activity.getString("start_date") + " au " + activity.getString("end_date")

                binding.root.visibility = View.VISIBLE
            },
            { error ->
                Toast.makeText(this, "pas bon", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "$authToken"
                return headers
            }
        }
        queue.add(requete)
    }
}

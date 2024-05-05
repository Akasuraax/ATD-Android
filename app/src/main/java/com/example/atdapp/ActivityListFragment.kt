package com.example.atdapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.atdapp.adapter.ActivityListAdapter
import com.example.atdapp.adapter.UserAdapter
import com.example.atdapp.model.ActivityList
import com.example.atdapp.model.User
import org.json.JSONArray

class ActivityListFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_activity_list, container, false)

        val sharedPreferences =
            requireActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", "")

        var queue = Volley.newRequestQueue(this.context)
        val requete: StringRequest = object : StringRequest(
            Request.Method.GET,
            Constant.API_BASE_URL + "/activity/between?startDate=2024-04-04T00:00:00%2B01:00&endDate=2024-04-17T00:00:00%2B01:00",
            { content ->

                var res = mutableListOf<ActivityList>()
                var activities = JSONArray(content)

                for (cpt in 0..<activities.length()) {

                    var currentjsonobj = activities.getJSONObject(cpt)
                    var a = ActivityList(
                        currentjsonobj.getInt("id"),
                        currentjsonobj.getString("title"),
                        currentjsonobj.getString("description"),
                        currentjsonobj.getString("address"),
                        currentjsonobj.getString("start"),
                        currentjsonobj.getString("end"),
                        currentjsonobj.getString("type_name"),
                        currentjsonobj.getString("color")
                    )
                    res.add(a)
                }

                var lv = view.findViewById<ListView>(R.id.activityList)
                var adp = ActivityListAdapter(this.context, res)

                lv.adapter = adp
            },
            { error ->
                Toast.makeText(this.context, "pas bon", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "$authToken"
                return headers
            }
        }
        queue.add(requete)
        return view
    }
}
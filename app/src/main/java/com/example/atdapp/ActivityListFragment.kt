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

        val sharedPreferences = requireActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", "")
        val userId = sharedPreferences.getString("userId", "")
        var queue = Volley.newRequestQueue(this.context)
        val requete: StringRequest = object : StringRequest(
            Request.Method.GET,
            Constant.API_BASE_URL + "/activity/withParticipate?userId=${userId}",
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

                lv.setOnItemClickListener { parent, adapter, position, id ->

                    var activity:ActivityList = adp.getItem(position)

                    var i = Intent(this.context,ActivityDetails::class.java)

                    i.putExtra("id", activity.id)
                    startActivity(i)
                }

                lv.adapter = adp
            },
            { error ->
                Toast.makeText(this.context, "Une erreur est survenue", Toast.LENGTH_LONG).show()
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
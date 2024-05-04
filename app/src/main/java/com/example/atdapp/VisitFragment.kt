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
import com.example.atdapp.adapter.UserAdapter
import com.example.atdapp.model.User
import org.json.JSONArray


class VisitFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val sharedPreferences = requireActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", "")

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_visit, container, false)
        var queue = Volley.newRequestQueue(this.context)
        val requete: StringRequest = object :StringRequest(Request.Method.GET,
            Constant.API_BASE_URL + "/user/visitall",
            { content ->

                var res = mutableListOf<User>()
                var users = JSONArray(content)

                for(cpt in 0..<users.length()) {

                    var currentjsonobj = users.getJSONObject(cpt)
                    var u = User(
                        currentjsonobj.getInt("id"),
                        currentjsonobj.getString("name"),
                        currentjsonobj.getString("forname"),
                        currentjsonobj.getBoolean("visit"),
                        currentjsonobj.getString("address")
                    )
                    res.add(u)
                }

                var lv = view.findViewById<ListView>(R.id.visitList)
                var adp = UserAdapter(this.context, res)

                lv.adapter = adp
                lv.setOnItemClickListener { parent, adapter, position, id ->

                    var user:User = adp.getItem(position)

                    var i = Intent(this.context,WriteNfcActivity::class.java)
                    i.putExtra("id",user.id)
                    i.putExtra("name",user.name)
                    i.putExtra("forname",user.forname)

                    startActivity(i)
                }
            },
            { error ->
                Toast.makeText(this.context,"pas bon", Toast.LENGTH_LONG).show()
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
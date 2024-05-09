package com.example.atdapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.atdapp.R
import com.example.atdapp.model.ActivityList
import com.example.atdapp.model.User

class ActivityListAdapter: BaseAdapter {

    var context: Context? = null
    var listActivity: MutableList<ActivityList>

    constructor(context: Context?, listActivity: MutableList<ActivityList>) : super() {
        this.context = context
        this.listActivity = listActivity
    }

    override fun getCount(): Int {
        return this.listActivity.size
    }

    override fun getItem(position: Int): ActivityList {
        return this.listActivity.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ResourceType")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v: View
        if (convertView == null) {
            var inflater: LayoutInflater = LayoutInflater.from(this.context)
            v = inflater.inflate(R.layout.row_activity, null)
        } else {
            v = convertView
        }

        var current: ActivityList = getItem(position)

        v.findViewById<TextView>(R.id.tv_activityTitle).text = current.title
        v.findViewById<TextView>(R.id.tv_activityDescription).text = current.description
        v.findViewById<TextView>(R.id.tv_activityAddress).text = current.address

        v.findViewById<TextView>(R.id.tv_startDate).text = current.start
        v.findViewById<TextView>(R.id.tv_endDate).text = current.end

        try {
            val colorInt = Color.parseColor(current.color)
            v.findViewById<View>(R.id.v_barActivity).setBackgroundColor(colorInt)
        } catch (e: IllegalArgumentException) {
            val defaultBlueColor = ContextCompat.getColor(v.context, R.color.blue)
            v.findViewById<View>(R.id.v_barActivity).setBackgroundColor(defaultBlueColor)
        }

        return v
    }
}
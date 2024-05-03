package com.example.atdapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.atdapp.R
import com.example.atdapp.model.User

class UserAdapter: BaseAdapter {

    var context: Context? = null
    var listUser: MutableList<User>

    constructor(context: Context?, list: MutableList<User>){
        this.context = context
        this.listUser = list
    }


    override fun getCount(): Int {
        return this.listUser.size
    }

    override fun getItem(position: Int): User {
        return this.listUser.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v: View
        if (convertView == null) {
            var inflater: LayoutInflater = LayoutInflater.from(this.context)
            v = inflater.inflate(R.layout.row_user, null)
        } else {
            v = convertView
        }

        var current:User = getItem(position) as User

        v.findViewById<TextView>(R.id.tv_userName).text = current.name
        v.findViewById<TextView>(R.id.tv_userforname).text = current.forname
        v.findViewById<TextView>(R.id.tv_userAddress).text = current.address

        if(current.visit)
            v.findViewById<View>(R.id.v_barVisit).setBackgroundResource(R.color.red)
        else
            v.findViewById<View>(R.id.v_barVisit).setBackgroundResource(R.color.green)

        return v
    }
}
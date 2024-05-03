package com.example.atdapp.model

class User {

    var id:Int = 0;
    var name:String = ""
    var forname:String = ""
    var visit:Boolean = false
    var address:String = ""
    constructor(id:Int, name: String, forname: String,visit: Boolean, address: String) {
        this.id = id
        this.name = name
        this.forname = forname
        this.visit = visit
        this.address = address
    }
}
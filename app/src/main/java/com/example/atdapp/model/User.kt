package com.example.atdapp.model

class User {

    var name:String = ""
    var forname:String = ""
    var visit:Boolean = false
    var address:String = ""
    constructor(name: String, forname: String,visit: Boolean, address: String) {
        this.name = name
        this.forname = forname
        this.visit = visit
        this.address = address
    }
}
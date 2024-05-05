package com.example.atdapp.model

class ActivityList {

    var id:Int = 0
    var title:String = ""
    var description:String = ""
    var address:String = ""
    var start:String =""
    var end:String = ""
    var typeName:String = ""
    var color:String = ""


    constructor(
        id: Int,
        title: String,
        description: String,
        address: String,
        start: String,
        end: String,
        typeName: String,
        color: String
    ) {
        this.id = id
        this.title = title
        this.description = description
        this.address = address
        this.start = start
        this.end = end
        this.typeName = typeName
        this.color = color
    }



}
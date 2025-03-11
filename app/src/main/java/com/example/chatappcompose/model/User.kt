package com.example.chatappcompose.model

data class User(val id:String,val username:String,val email:String,val password:String,val gender:String,val image:String) {
    constructor():this("","","","","","")
}
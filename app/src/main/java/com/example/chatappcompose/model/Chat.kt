package com.example.chatappcompose.model

data class Chat(val message:String,val idSender:String,val idReceiver:String,val time:String){
    constructor():this("","","","")
}

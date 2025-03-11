package com.example.chatappcompose.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserApi {
    private val retrofit=Retrofit.Builder()
        .baseUrl("http://192.168.1.6:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService= retrofit.create(ApiService::class.java)
}
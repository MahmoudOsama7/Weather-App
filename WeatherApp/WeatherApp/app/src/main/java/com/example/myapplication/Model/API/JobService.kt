package com.example.myapplication.Model.API

import com.example.myapplication.Model.Pojo.Json.MyJsonStructure
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface JobService {

    //weather API Interface
    @GET("weather?&units=metric&appid=63c50eb5d8911e292b66d89189d48bed")
    fun getPostByUsingQuery(@Query("q") city:String):Call<MyJsonStructure>

    @GET("weather?&units=metric&appid=63c50eb5d8911e292b66d89189d48bed")
    fun getPostUsingQueryLocation(@Query("lat")lan:String,@Query("lon")lat:String):Call<MyJsonStructure>
}
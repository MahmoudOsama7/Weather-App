package com.example.myapplication.Model.API


import com.example.myapplication.Model.Pojo.Json.MyJsonStructure
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object PostClient
{
    private val jobService:JobService
    private var jobService1:JobService
    private const val BASE_URL="https://api.openweathermap.org/data/2.5/"

    init
    {

        val retrofit1:Retrofit=Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
        this.jobService1=retrofit1.create(JobService::class.java)

        val retrofit:Retrofit=Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        this.jobService =retrofit.create(JobService::class.java)
    }

    fun getWeatherData(city:String):Call<MyJsonStructure>
    {
        return jobService1.getPostByUsingQuery(city)
    }
    fun getWeatherDataByLocation(lat:String,lon:String):Call<MyJsonStructure>
    {
        return jobService.getPostUsingQueryLocation(lat,lon)
    }
}
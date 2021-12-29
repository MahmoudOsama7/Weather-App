package com.example.myapplication.Model.Pojo

import android.util.Log
import com.example.myapplication.Contract.MyInterface
import com.example.myapplication.Model.API.PostClient
import com.example.myapplication.Model.Pojo.Json.MyJsonStructure
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class WeatherDataModel :MyInterface.WeatherDataModel{


    private var presenter: MyInterface.Presenter? = null
    private var temp :String?=null
    private var temp1 :String?=null
    private var temp2 :String?=null

    private var tempLocation:String?=null
    private var tempMinLocation:String?=null
    private var tempMaxLocation:String?=null
    private var cityName:String?=null


    override fun getValueByLocation(lan: String, lon: String) {
        PostClient.getWeatherDataByLocation(lan,lon).enqueue(object : Callback<MyJsonStructure> {
            override fun onResponse(
                call: Call<MyJsonStructure>,
                response: Response<MyJsonStructure>
            ) {
                tempLocation = response.body()?.main?.temp.toString()
                tempMinLocation = response.body()?.main?.temp_min.toString()
                tempMaxLocation = response.body()?.main?.temp_max.toString()
                cityName = response.body()?.name
                presenter?.returnDataOfLocation(
                    tempLocation!!,
                    tempMinLocation!!,
                    tempMaxLocation!!,
                    cityName!!
                )
                Log.d("3ash", lan)
                Log.d("3ash", lon)
            }

            override fun onFailure(call: Call<MyJsonStructure>, t: Throwable) {

            }
        })
    }

    override fun getValue(city:String) {


        PostClient.getWeatherData(city).enqueue(object : Callback<MyJsonStructure> {
            override fun onResponse(
                call: Call<MyJsonStructure>,
                response: Response<MyJsonStructure>
            ) {
                temp = response.body()?.main?.temp.toString()
                temp1 = response.body()?.main?.temp_min.toString()
                temp2 = response.body()?.main?.temp_max.toString()
                presenter?.returnData(temp!!, temp1!!, temp2!!)
            }

            override fun onFailure(call: Call<MyJsonStructure>, t: Throwable) {
                Log.d("Error", "Failed To Connect")
            }

        })
    }



    override fun init(presenter: MyInterface.Presenter) {
        this.presenter=presenter
    }
}


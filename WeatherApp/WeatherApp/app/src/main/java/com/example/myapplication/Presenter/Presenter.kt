package com.example.myapplication.Presenter

import com.example.myapplication.Contract.MyInterface
import com.example.myapplication.Model.Pojo.DataModel
import com.example.myapplication.Model.Pojo.WeatherDataModel

class Presenter(_view:MyInterface.DataView):MyInterface.Presenter {
    //passing the interface from the ui to be used by the presenter as to state the ui interface
    private var view:MyInterface.DataView=_view
    //the list of that the user will update it here in the presenter that to be passed to the model to be added
    private var model: DataModel = DataModel()
    //"Weather Model Class to retrieve the JSon data from weatherMapAPI
    private var weatherModel:WeatherDataModel=WeatherDataModel()


    init
    {

        model.initData(this)
        weatherModel.init(this)
    }
    //get Data after any update
    override fun getCountryLists(value:ArrayList<String>)
    {
        view.viewInit(value)
    }

    override fun getData(title: String) {
        weatherModel.getValue(title)
    }

    override fun returnData(temp:String,temp1:String,temp2:String) {
        view.updateViews(temp,temp1,temp2)
    }

    override fun returnDataOfLocation(temp: String, temp1: String, temp2: String, city: String) {
        view.updateViewsByLocation(temp,temp1,temp2,city)
    }

    override fun takeLocationDataFromView(latitude: String, longitude: String) {
        weatherModel.getValueByLocation(latitude,longitude)
    }


}
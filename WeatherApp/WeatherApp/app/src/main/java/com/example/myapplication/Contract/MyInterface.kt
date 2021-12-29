package com.example.myapplication.Contract

interface MyInterface {
    //interface of the View
    interface DataView{
        fun viewInit(value:ArrayList<String>) //presenter will use it to take the array value from the view
        fun updateViews(temp:String,temp1:String,temp2:String)
        fun autoCompleteTextView()
        fun giveLocationDataToPresenter()
        fun updateViewsByLocation(temp: String, temp1: String, temp2: String, city: String)
        fun getLocation()
    }
    //interface for the presenter
    interface Presenter{
        fun getCountryLists(value:ArrayList<String>)
        fun getData(title:String)
        fun returnData(temp:String,temp1:String,temp2:String)
        fun returnDataOfLocation(temp:String,temp1:String,temp2:String,city:String)
        fun takeLocationDataFromView(latitude:String, longitude:String)
    }
    //interface for the model
    interface ModelInterface{
        fun getValue()//presenter will use it to return the value of list in the class DataModel after adding new data
        fun updateList()
    }
    interface WeatherDataModel
    {
        fun getValueByLocation(lan:String,lon:String)
        fun getValue(city:String)
        fun init(presenter: Presenter)
    }
}
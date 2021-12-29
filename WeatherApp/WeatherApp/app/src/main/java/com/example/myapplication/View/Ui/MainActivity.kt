package com.example.myapplication.View.Ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.myapplication.Contract.MyInterface
import com.example.myapplication.Presenter.Presenter
import com.example.myapplication.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),MyInterface.DataView,MyInterface{

    private val permissions= arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private var presenter:Presenter?=null
    private var selectedData:String?=null
    private var list:ArrayList<String>?=null
    private lateinit var locationManager: LocationManager
    private var hasGps=false
    private var hasNetwork=false
    private var locationNetwork:Location?=null
    private var tvTemp2:TextView?=null
    private var tvTempmin2:TextView?=null
    private var tvTempmax2:TextView?=null
    private var dialog:AlertDialog?=null
    private var autoCompleteTextView:AutoCompleteTextView?=null



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter= Presenter(this)

    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }

    override fun viewInit(value:ArrayList<String>)
    {
        tvTemp2=findViewById(R.id.tv_Temp2)
        tvTempmin2=findViewById(R.id.tv_tempMin2)
        tvTempmax2=findViewById(R.id.tv_tempMax2)
        autoCompleteTextView=findViewById(R.id.attv)
        val myAdapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,value)
        attv.setAdapter(myAdapter)
        list=value
        autoCompleteTextView()
    }

    override fun updateViews(temp:String,temp1:String,temp2:String) {
        tvTemp2?.text="$temp °C"
        tvTempmin2?.text="$temp1 °C"
        tvTempmax2?.text="$temp2 °C"
    }

    override fun autoCompleteTextView()
    {
        attv.setOnItemClickListener{ parent, view, position, id ->
            selectedData = parent.getItemAtPosition(position) as String
            presenter?.getData(selectedData!!)
        }
    }

    override fun giveLocationDataToPresenter() {
        presenter?.takeLocationDataFromView(locationNetwork?.latitude.toString(),locationNetwork?.longitude.toString())
    }

    override fun updateViewsByLocation(temp: String, temp1: String, temp2: String, city: String) {
        tvTemp2?.text="$temp °C"
        tvTempmin2?.text="$temp1 °C"
        tvTempmax2?.text="$temp2 °C"
        autoCompleteTextView?.setText(city)
    }

    override fun getLocation()  {

        //Thread.sleep(4000)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps && hasNetwork) {
            if (hasNetwork) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)

                    {
                    ActivityCompat.requestPermissions(this@MainActivity,permissions,1)
                    return
                }
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F
                ) { location ->
                    locationNetwork = location
                }
                val localNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetwork != null) {
                    locationNetwork = localNetwork
                    Log.d("locationNew", locationNetwork?.latitude.toString())
                    Log.d("locationNew", locationNetwork?.longitude.toString())
                    giveLocationDataToPresenter()
                }
            }
        }
        else if(!hasGps)
        {
            Toast.makeText(this@MainActivity, "we need GPS", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(this@MainActivity, "Granted", Toast.LENGTH_SHORT).show()
        }
        return
    }



}
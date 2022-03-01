package com.example.myapplication.View.Ui

import android.Manifest
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.Contract.MyInterface
import com.example.myapplication.Presenter.Presenter
import com.example.myapplication.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MyInterface.DataView, MyInterface {

    private var presenter: Presenter? = null
    private var selectedData: String? = null
    private var list: ArrayList<String>? = null
    private var tvTemp2: TextView? = null
    private var tvTempmin2: TextView? = null
    private var tvTempmax2: TextView? = null
    private var dialog: AlertDialog? = null
    private var autoCompleteTextView: AutoCompleteTextView? = null
    private var locationManager: LocationManager? = null
    private val REQUEST_CHECK_SETTINGS = 12 //any value i want
    private val REQUEST_CODE = 1
    private val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = Presenter(this)
    }

    override fun onResume() {
        super.onResume()
        getLocationOnlyOnce()
    }

    override fun viewInit(value: ArrayList<String>) {
        tvTemp2 = findViewById(R.id.tv_Temp2)
        tvTempmin2 = findViewById(R.id.tv_tempMin2)
        tvTempmax2 = findViewById(R.id.tv_tempMax2)
        autoCompleteTextView = findViewById(R.id.attv)
        val myAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, value)
        attv.setAdapter(myAdapter)
        list = value
        autoCompleteTextView()
    }

    override fun updateViews(temp: String, temp1: String, temp2: String) {
        tvTemp2?.text = "$temp °C"
        tvTempmin2?.text = "$temp1 °C"
        tvTempmax2?.text = "$temp2 °C"
    }

    override fun autoCompleteTextView() {
        attv.setOnItemClickListener { parent, view, position, id ->
            selectedData = parent.getItemAtPosition(position) as String
            presenter?.getData(selectedData!!)
        }
    }

    override fun giveLocationDataToPresenter(latitude: String, longitude: String) {
        presenter?.takeLocationDataFromView(latitude, longitude)
    }

    override fun updateViewsByLocation(temp: String, temp1: String, temp2: String, city: String) {
        tvTemp2?.text = "$temp °C"
        tvTempmin2?.text = "$temp1 °C"
        tvTempmax2?.text = "$temp2 °C"
        autoCompleteTextView?.setText(city)
    }

    override fun requestPermission() {
        //ActivityCompat.shouldShowRequestPermissionRationale is true when all the permissions is not granted
        //and they are not granted when ActivityCompat.checkSelfPermission is !=granted
        if ((ActivityCompat.shouldShowRequestPermissionRationale(
                this@MainActivity,
                permission[0]
            ) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
        ) {
            val alert = AlertDialog.Builder(this@MainActivity)
            alert.setTitle("Grant Those Permission")
            alert.setMessage("The Following Permissions Needs Acceptance Please")
                .setPositiveButton("OK")
                { dialog, which ->
                    ActivityCompat.requestPermissions(this@MainActivity, permission, REQUEST_CODE)
                    dialog.dismiss()
                }
            alert.setNegativeButton("Cancel")
            { dialog, which ->
                Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            alert.create()
            alert.show()
        } else {
            //this is to be called first time because ActivityCompat.shouldShowRequestPermissionRationale is false still
            //if we deny from requestPermissions here , then ActivityCompat.shouldShowRequestPermissionRationale will be = true
            ActivityCompat.requestPermissions(
                this@MainActivity,
                permission,
                REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray)
    {
        var requestCode = requestCode
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (1.also { requestCode = it }) {
            REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@MainActivity, grantResults[0].toString() + "", Toast.LENGTH_SHORT).show() //value of permission if granted
                    Toast.makeText(this@MainActivity, "Permission For Location Is Granted", Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    Toast.makeText(this@MainActivity, grantResults[0].toString() + "", Toast.LENGTH_SHORT).show() ///value of permission if denied
                    Toast.makeText(this@MainActivity, "Permission For Location Is Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun turnGpsOn() {
        val locationRequest = LocationRequest.create() //initialize the location Request parameter
        locationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY //to get the most accurate location
        locationRequest.interval = 5000 //interval of updating location each 5 seconds
        locationRequest.fastestInterval = 2000
        //creating a locationSettingRequest Builder that contain all our locationRequest attributes in addLocationRequest
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        //check the result of requesting the location if location is enabled or not
        val request = LocationServices.getSettingsClient(
            applicationContext
        ).checkLocationSettings(builder.build())
        //we created a task of type Task<LocationSettingsResponse> called result : stating that our task to check if we can turn the
        //(GPS on ) , the object we created request of type Task<LocationSettingsResponse> has onComplete listerner stating that once the task
        //is completed it will show the result of our task by using the attribute task found in the onCompleteLister in the object request
        //the result of task.getResult() is received in new object of type LocationSettingsResponse and we named result
        //that is LocationSettingsResponse result = task.getResult();
        //however we need to cast it as ApiException.class) (like in retrofit we say api = retrofit.create(Api.class))
        //the response of what happened here will be shown at the onActivityResult like the onPermissionRequestResult in the permissions
        //either the gps is turned on or off by the user
        request.addOnCompleteListener { task ->
            try {
                //if this one is fulfilled , meaning that the device location (GPS) is already on ,
                val result = task.getResult(ApiException::class.java)
                Toast.makeText(this@MainActivity, "GPS Is ON", Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {
                //the location is not on for some cases we will check on in the switch case
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        //here we will resolve the ApiException that is generated by task.getResult(ApiException.class)
                        //we will case the e to a another class type called (ResolvableApiException)
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            this@MainActivity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (ex: SendIntentException) {
                        //no need to do anything in the catch statement
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Toast.makeText(
                        this@MainActivity,
                        "NO GPS IN THE DEVICE",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getLocationOnlyOnce() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val hasNetwork = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasNetwork) {
            if ((ContextCompat.checkSelfPermission(this@MainActivity, permission[0]))//-1) //-1
                == PackageManager.PERMISSION_GRANTED
            )//-3
            {
                locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f,
                    object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            Toast.makeText(
                                this@MainActivity,
                                "Latitude:" + location.latitude,
                                Toast.LENGTH_SHORT
                            ).show()
                            Toast.makeText(
                                this@MainActivity,
                                "Longitude:" + location.longitude,
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Success", "onLocationChanged: " + location.latitude)
                            Log.d("Success", "onLocationChanged: " + location.longitude)
                            giveLocationDataToPresenter(
                                location.latitude.toString(),
                                location.longitude.toString()
                            )
                            //after getting updates only once , the we remove updates by giving the method remove updates the same listener which we refer here
                            //as this since we are in the context of new LocationListener()
                            locationManager!!.removeUpdates(this)
                        }
                    })
            }
        }
    }
}
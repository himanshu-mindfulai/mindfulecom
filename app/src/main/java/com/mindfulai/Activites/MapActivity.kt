package com.mindfulai.Activites

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mindfulai.Utils.CommonUtils
import com.mindfulai.Utils.SPData
import com.mindfulai.ministore.R
import com.mindfulai.ui.LocationBottomSheet
import com.valdesekamdem.library.mdtoast.MDToast
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private var city: String? = null
    private var state: String? = null
    private var postalCode: String? = null
    private var mMap: GoogleMap? = null
    private var btnConfirm: Button? = null;
    lateinit var btnManual:Button
    private var tvAddress: TextView? = null;
    private lateinit var mLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        title = "Pick a location"
        supportActionBar!!.hide()
        btnManual = findViewById(R.id.bt_manual)
        btnManual.setOnClickListener {
            entermanually()
        }

        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                    (this as Activity?)!!,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 123)
        } else {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gps = false
            var network = false
            try {
                gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            try {
                network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            if (!gps && !network) {
                val bottomSheet = LocationBottomSheet()
                bottomSheet.show(
                        supportFragmentManager,
                        "LocationBS"
                )
            } else {
                init()
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        var gps = false
//        var network = false
//        try {
//            gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//        try {
//            network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//        if (!gps && !network) {
//            val bottomSheet = LocationBottomSheet()
//            bottomSheet.show(
//                    supportFragmentManager,
//                    "LocationBS"
//            )
//        } else {
//            init()
//        }
    }

    fun entermanually() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun init(){
        Log.e("MapActivity", "Lat0: " + SPData.getAppPreferences().latitude)
        btnManual = findViewById(R.id.bt_manual)
        btnConfirm = findViewById(R.id.bt_confirm_address);
        tvAddress = findViewById(R.id.tv_address);

        btnManual.setOnClickListener {
            entermanually()
        }
        findViewById<ImageView>(R.id.back).setOnClickListener {
            entermanually()
        }
        try {
            mLatLng = LatLng(SPData.getAppPreferences().latitude.toDouble(), SPData.getAppPreferences().longitude.toDouble())
        } catch (e : NumberFormatException){
            try{
                mLatLng = CommonUtils.getCurrentLocation(this)
            } catch (e: Exception){
                MDToast.makeText(this, "Couldn't get location right now!").show()
            }
        } catch (e : Exception){
            MDToast.makeText(this, "Can't get location", MDToast.TYPE_INFO).show()
        }
        btnConfirm?.setOnClickListener {
            if(tvAddress?.text?.equals(resources.getString(R.string.map_activity_tv_placeholder))!! ||
                    tvAddress?.text?.equals("Not found (Try clicking on the map on your location)")!!){
                MDToast.makeText(applicationContext, "Please select a location", Toast.LENGTH_SHORT ,MDToast.TYPE_INFO).show()
            } else {
                var intent: Intent = Intent()
                SPData.getAppPreferences().latitude = mLatLng.latitude.toString()
                SPData.getAppPreferences().longitude = mLatLng.longitude.toString()
                intent.putExtra("address", tvAddress?.text.toString())
                intent.putExtra("city", city);
                intent.putExtra("state", state);
                intent.putExtra("pinCode", postalCode);
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 123) {
            if (permissions.isNotEmpty() && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                var gps = false
                var network = false
                try {
                    gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                try {
                    network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                if (!gps && !network) {
                    val bottomSheet = LocationBottomSheet()
                    bottomSheet.show(
                            supportFragmentManager,
                            "LocationBS"
                    )
                } else {
                    init()
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MapActivity", "setUpMap:: Permission not given")
            return
        }
        Log.e("MapActivity", "onMapReady::")
        try {
            mMap = googleMap
            mMap!!.isMyLocationEnabled = true
            mMap!!.setOnMyLocationChangeListener(OnMyLocationChangeListener { location ->
                val ltlng = LatLng(location.latitude, location.longitude)
                mLatLng = ltlng
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        ltlng, 16f)
                mMap!!.animateCamera(cameraUpdate)
            })
            val markerOptions = MarkerOptions()
            markerOptions.icon(getMarkerIconFromDrawable(resources.getDrawable(R.drawable.map_marker)))
//            mMap!!.setOnMyLocationChangeListener {
//                mLatLng = LatLng(it.latitude, it.longitude)
//            }
            try{
                markerOptions.position(mLatLng)
                markerOptions.title(getAddress(mLatLng))
//            mMap!!.clear()
                val location = CameraUpdateFactory.newLatLngZoom(mLatLng, 15f)
                mMap!!.animateCamera(location)
                mMap!!.addMarker(markerOptions)
            } catch (e: Exception){
                e.printStackTrace()
                tvAddress?.text = "Not found (Try clicking on the map on your location)";
            }
            mMap!!.setOnMapClickListener(OnMapClickListener { latLng ->
                mLatLng = latLng
                markerOptions.position(latLng)
                try {
                    markerOptions.title(getAddress(latLng))
                } catch (e: Exception){
                    e.printStackTrace()
                    markerOptions.title("Not found")
                }
                mMap!!.clear()
                val location = CameraUpdateFactory.newLatLngZoom(
                        latLng, 15f)
                mMap!!.moveCamera(location)
                mMap!!.addMarker(markerOptions)
            })
        } catch (e: Exception){
            Log.e("Shivamvk###", "Map auth failed")
            e.printStackTrace()
        }
    }

    private fun getMarkerIconFromDrawable(drawable: Drawable): BitmapDescriptor? {
        val canvas = Canvas()
        val bitmap: Bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight())
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getAddress(latLng: LatLng): String?{
        val addresses: List<Address>
        val geocode: Geocoder = Geocoder(this, Locale.getDefault())
        return try {
            addresses = geocode.getFromLocation(latLng.latitude, latLng.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses[0].locality
            state = addresses[0].adminArea
            //country = addresses[0].countryName
            postalCode = addresses[0].postalCode
            tvAddress?.text = addresses[0].featureName + ", " + city + " (${postalCode})"
//            val knownName = addresses[0].featureName
//            val ft = fragmentManager.beginTransaction()
//            val prev = fragmentManager.findFragmentByTag("dialog")
//            if (prev != null) {
//                ft.remove(prev)
//            }
//            ft.addToBackStack(null)
//            val dialogFragment = ConfirmAddress()
//            val args = Bundle()
//            args.putDouble("lat", latLng.latitude)
//            args.putDouble("long", latLng.longitude)
//            args.putString("address", address)
//            dialogFragment.arguments = args
//            dialogFragment.show(ft, "dialog")
            address
        } catch (e: Exception) {
            e.printStackTrace().toString()
            tvAddress?.text = "Not found (Try clicking on the map on your location)"
            "Not found!"
        }
    }
}
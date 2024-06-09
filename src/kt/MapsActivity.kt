package com.example.gezirehberi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.gezirehberi.databinding.ActivityMapsBinding
import com.example.gezirehberi.fragment.filteringFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, UpdateMarkersListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    var clickedEventID: String = ""
    private val auth = Firebase.auth
    private lateinit var currentLocation: LatLng
    private lateinit var clickedLocation: LatLng
    private var clicked = false
    var locationDetected: Boolean = false
    lateinit var currentMarker: Marker
    public lateinit var etkinlikKonumu: LatLng
    var eventMarker: Marker? = null
    private val db = Firebase.firestore
    val markerEtkinlikIDMap = mutableMapOf<Marker, String>()
    var filterScreenIsOpen : Boolean = false
    var filterFragmentStarted: Boolean = false
    lateinit var createEventButton : Button
    lateinit var viewEventButton : Button
    var markerClickListenerIsReady : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        createEventButton = findViewById(R.id.bt_maps_createEvent)
        viewEventButton = findViewById(R.id.bt_maps_viewEvent)
        createEventButton.visibility = View.INVISIBLE
        viewEventButton.visibility = View.INVISIBLE

    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.setOnMarkerClickListener(listener2)
        db.collection("events").get().addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                val documents = snapshot.documents
                val markers : MutableList<GeoPoint> = mutableListOf()
                for (document in documents) {
                    markers.add(document.get("locationLatLng") as GeoPoint)
                    updateMarkers(markers)

                    /*val eventsLocationsGeoPoint = document["locationLatLng"] as GeoPoint
                    val eventsLocations =
                        LatLng(eventsLocationsGeoPoint.latitude, eventsLocationsGeoPoint.longitude)
                    val eventsTitles = document["eventTitle"] as String
                    try {
                        marker = mMap.addMarker(
                            MarkerOptions().position(eventsLocations).title(eventsTitles)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.eventicon))
                        )
                        eventMarkerList.add(marker!!)
                        markerEtkinlikIDMap[marker!!] = document["eventID"] as String
                    } catch (e: Exception) {
                        println(e.localizedMessage)
                    }*/
                }
            }
        }
        mMap.setOnMapLongClickListener(listener)
        mMap.setOnMapClickListener(listener3)
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {
                if (locationDetected) {
                    currentMarker.remove()
                }
                currentLocation = LatLng(p0.latitude, p0.longitude)

                currentMarker = mMap.addMarker(
                    MarkerOptions().position(currentLocation).title("Buradasınız.")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                )!!
                markerClickListenerIsReady = true
                if (!locationDetected) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14f))
                }
                locationDetected = true
            }

        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //izin verilmedi
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else { // izin verilmiş
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000,
                10f,
                locationListener
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    //izin verildi
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        10000,
                        10f,
                        locationListener
                    )

                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val listener = object : GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng) {
            if (clicked) {
                eventMarker?.remove()
            }
            clickedLocation = LatLng(p0.latitude, p0.longitude)

            val geoCoder = Geocoder(this@MapsActivity, Locale.getDefault())
            try {
                createEventButton.visibility = View.VISIBLE
                val adressList = geoCoder.getFromLocation(p0.latitude, p0.longitude, 1)
                clicked = true
                etkinlikKonumu = clickedLocation
                eventMarker = mMap.addMarker(
                    MarkerOptions().position(clickedLocation)
                        .title(adressList?.get(0)?.getAddressLine(0)).draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder))
                )!!
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    val listener2 = object : GoogleMap.OnMarkerClickListener {
        override fun onMarkerClick(p0: Marker): Boolean {
            if(markerClickListenerIsReady){
                if (p0 == currentMarker) {
                    p0.showInfoWindow()
                } else if (eventMarker != null) {
                    if (p0 == eventMarker) {
                        p0.showInfoWindow()
                    }
                }else {
                    try {
                        viewEventButton.visibility = View.VISIBLE
                        clickedEventID = markerEtkinlikIDMap[p0]!!
                        p0.showInfoWindow()

                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            "Marker listede değil: " + e.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            return true
        }
    }
    val listener3 = object : GoogleMap.OnMapClickListener {
        override fun onMapClick(p0: LatLng) {
            eventMarker?.remove()
            eventMarker = null
            createEventButton.visibility = View.INVISIBLE
            viewEventButton.visibility = View.INVISIBLE
        }
    }
    fun etkinlikOlustur(view: View) {
        if (clicked) {
            val intent = Intent(this, CreateEventActivity::class.java)
            intent.putExtra("Adress", etkinlikKonumu)
            startActivity(intent)
        } else {
            Toast.makeText(
                applicationContext,
                "Lütfen önce basılı tutarak konum seçin",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    fun viewTheEvent(view: View){
        if (clickedEventID == "") {
            Toast.makeText(applicationContext, "Lütfen önce etkinlik seçin", Toast.LENGTH_LONG)
                .show()
            return
        } else {
            val intent = Intent(this, ViewTheEventActivity::class.java)
            intent.putExtra("eventID", clickedEventID)
            startActivity(intent)
        }
    }
    fun showFilterFragment(view: View) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val filteringFragment = filteringFragment()  // Class name should start with an uppercase letter
        if (!filterFragmentStarted){
            fragmentTransaction.replace(R.id.fl_filter, filteringFragment)
            fragmentTransaction.addToBackStack(null)  // Allow user to use back button to close the fragment
            fragmentTransaction.commit()
            filterScreenIsOpen = true
            filterFragmentStarted = true
        }

        if (!filterScreenIsOpen) {
            val existingFragment = fragmentManager.findFragmentById(R.id.fl_filter)
            if (existingFragment != null) {
                fragmentTransaction.show(existingFragment).commit()
                filterScreenIsOpen = true
            }
        } else {
            val existingFragment = fragmentManager.findFragmentById(R.id.fl_filter)
            if (existingFragment != null) {
                fragmentTransaction.hide(existingFragment).commit()
                filterScreenIsOpen = false
            }
        }
    }
    fun goMyLocation(view: View){
        if(markerClickListenerIsReady){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14f))
        }
    }
    override fun updateMarkers(markerLocations: MutableList<GeoPoint>) {
        filterScreenIsOpen = false
        for(marker in markerEtkinlikIDMap.keys){
            marker.remove()
        }
        markerEtkinlikIDMap.clear()
        val chunkedMarkerLocations = markerLocations.chunked(10)
        for (chunk in chunkedMarkerLocations){
            db.collection("events").whereIn("locationLatLng", chunk).get().addOnSuccessListener { snapshot->
                if (!snapshot.isEmpty && snapshot != null){
                    for (document in snapshot.documents) {
                        val markerLocation = document["locationLatLng"] as GeoPoint
                        val eventTitle = document["eventTitle"] as String
                        val eventID = document["eventID"] as String
                        val markerLocationLatLng = LatLng(markerLocation.latitude, markerLocation.longitude)

                        try {
                            val marker = mMap.addMarker(
                                MarkerOptions().position(markerLocationLatLng).title(eventTitle)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.eventicon))
                            )
                            if (marker != null) {
                                markerEtkinlikIDMap[marker] = eventID
                            }
                        } catch (e: Exception) {
                            println(e.localizedMessage)
                        }
                    }
                }
            }
        }
    }
}
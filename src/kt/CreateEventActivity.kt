package com.example.gezirehberi

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID


class CreateEventActivity : AppCompatActivity() {
    lateinit var adressLatLng : LatLng
    val auth = Firebase.auth
    val db = Firebase.firestore
    lateinit var adressList : List<Address>
    lateinit var adressGeoPoint : GeoPoint
    lateinit var spinner: Spinner
    lateinit var spinnerCities : Spinner
    lateinit var checkBoxFree : CheckBox
    lateinit var checkBoxUnlimited : CheckBox
    lateinit var eventPrice : EditText
    lateinit var eventParticipantLimit : EditText
    var eventIsFree : Boolean = false
    var eventIsUnlimited : Boolean = false
    lateinit var selectedCategory : String
    //lateinit var selectedCity : String
    var city : String? = null


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_event)
        checkBoxFree = findViewById(R.id.cb_free)
        checkBoxUnlimited = findViewById(R.id.cb_unlimited)
        eventPrice = findViewById(R.id.et_price)
        eventParticipantLimit = findViewById(R.id.et_participantLimit)
        adressLatLng = intent.getParcelableExtra("Adress",LatLng::class.java)!!
        adressGeoPoint = GeoPoint(adressLatLng.latitude,adressLatLng.longitude)

        spinner = findViewById(R.id.spinner_categories)
        //spinnerCities = findViewById(R.id.spinner_cities)
        val arrayAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            R.layout.dropdown_item).also { adapter->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCategory = p0!!.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        /*
        val arrayAdapterCities = ArrayAdapter.createFromResource(
            this,
            R.array.cities_array,
            R.layout.dropdown_item).also { adapter->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCities.adapter = arrayAdapterCities
        spinnerCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCity = p0!!.getItemAtPosition(p2).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }*/
        checkBoxFree.setOnCheckedChangeListener {buttonView, isCheck->
            if (isCheck){
                eventIsFree = true
                eventPrice.isEnabled = false
            }else{
                eventIsFree = false
                eventPrice.isEnabled = true
            }
        }
        checkBoxUnlimited.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                eventIsUnlimited = true
                eventParticipantLimit.isEnabled = false
            }else{
                eventIsUnlimited = false
                eventParticipantLimit.isEnabled = true
            }
        }
    }


    override fun onStart() {
        super.onStart()
        val geoCoder = Geocoder(this@CreateEventActivity, Locale.getDefault())

        try {
            adressList = geoCoder.getFromLocation(adressLatLng!!.latitude,adressLatLng!!.longitude,1)!!
            findViewById<TextView>(R.id.tv_etkinlikAdresi).text = "Adres: ${adressList?.get(0)?.getAddressLine(0)}"
            val regex = Regex("(?<=[/])([^,]+)(?=[,])")
            val matches = regex.findAll(adressList.get(0).getAddressLine(0))
            for (match in matches){
                city = match.value.split(" ").firstOrNull()
            }
        }catch(e : Exception){
            Toast.makeText(applicationContext,e.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }


    fun etkinligiOlustur(view : View){



        val price : Int = if (eventIsFree || eventPrice.text.toString() == "") 0 else eventPrice.text.toString().toInt()
        val participantLimit : Int = if (eventIsUnlimited || eventParticipantLimit.text.toString() == "") 99999 else eventParticipantLimit.text.toString().toInt()
        val eventDate = findViewById<EditText>(R.id.et_etkinlikTarihi)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = dateFormat.parse(eventDate.text.toString())
        val eventTitle = findViewById<EditText>(R.id.et_etkinlikAdi)
        val eventDetails = findViewById<EditText>(R.id.et_etkinlikAciklamasi)
        var creationTime = Timestamp.now()
        if (adressList.get(0) != null && eventDate.toString() != "" && eventTitle.toString() != ""
            && eventDetails.toString() != ""){
            val eventID = UUID.randomUUID().toString();
            val eventInfos = hashMapOf(
                "eventID" to eventID,
                "location" to adressList.get(0).getAddressLine(0),
                "city" to city,
                "locationLatLng" to adressGeoPoint,
                "creator" to auth.currentUser!!.email,
                "eventDate" to date,
                "eventTitle" to eventTitle.text.toString(),
                "eventDetails" to eventDetails.text.toString(),
                "creationTime" to creationTime,
                "price" to price,
                "participantLimit" to participantLimit,
                "category" to selectedCategory
            )
            db.collection("events").document(eventID).set(eventInfos).addOnSuccessListener { DocumentReference->
                Toast.makeText(applicationContext,"Etkinlik Oluşturuldu", Toast.LENGTH_SHORT).show()
                println("Veri kaydedildi")
                finish()
            }.addOnFailureListener { Exception->
                Toast.makeText(applicationContext, Exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }else {
            Toast.makeText(applicationContext, "Boş Alan Bırakılamaz", Toast.LENGTH_SHORT).show()
        }

    }
}
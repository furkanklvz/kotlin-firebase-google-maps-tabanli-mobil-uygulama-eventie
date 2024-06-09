package com.example.gezirehberi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gezirehberi.adapter.RecyclerAdapterParticipants
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreatedEventDescriptionActivity : AppCompatActivity() {
    var participantsMail: MutableList<String> = mutableListOf()
    var participantsName: MutableList<String> = mutableListOf()
    var participantsSurname: MutableList<String> = mutableListOf()
    val db = Firebase.firestore
    lateinit var eventID : String

    lateinit var progressCircle: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_created_event_description)

        progressCircle = findViewById(R.id.pb_showParticipant)
        progressCircle.visibility = View.INVISIBLE


        val intent = intent
        eventID = intent.getStringExtra("eventID")!!
        val eventDate = intent.getStringExtra("eventDate")
        db.collection("events").document(eventID!!).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                findViewById<TextView>(R.id.tv_eventTitleeo).text = snapshot["eventTitle"] as String
                findViewById<TextView>(R.id.tv_eventDateeo).text = eventDate
                findViewById<TextView>(R.id.tv_eventAdresseo).text = snapshot["location"] as String
                findViewById<TextView>(R.id.tv_eventDescriptioneo).text =
                    snapshot["eventDetails"] as String
                if (snapshot.contains("participants")) {
                    participantsMail = snapshot["participants"] as MutableList<String>
                }
                val participantLimit = (snapshot["participantLimit"] as Number).toInt()
                val price = (snapshot["price"] as Number).toInt()
                findViewById<TextView>(R.id.tv_participantLimiteo).text =
                    if (participantLimit == 99999) "Sınırsız" else participantLimit.toString()
                findViewById<TextView>(R.id.tv_eventPriceeo).text =
                    if (price == 0) "Ücretsiz" else price.toString()
                findViewById<TextView>(R.id.tv_eventCategoryeo).text =
                    snapshot["category"] as String
            }
        }

        findViewById<Button>(R.id.bt_cancelEvent).setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Etkinliği İptal Et?")
            builder.setMessage("Etkinliği iptal etmek istediğinizden emin misiniz?")
            builder.setPositiveButton("Evet"){ dialogInterface, which ->
                db.collection("events").document(eventID).delete().addOnSuccessListener {task->

                    db.collection("users").whereArrayContains("joinedEvents", eventID).get().addOnSuccessListener { snapshot ->
                        for (document in snapshot.documents){
                            val joinedEvents = document["joinedEvents"] as MutableList<String>
                            joinedEvents.remove(eventID)
                            db.collection("users").document(document.id).update("joinedEvents", joinedEvents)

                        }
                    }
                    Toast.makeText(applicationContext,"Etkinlik iptal edildi", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener { e->
                    Toast.makeText(this,e.localizedMessage, Toast.LENGTH_SHORT).show()
                    return@addOnFailureListener
                }

            }
            builder.setNegativeButton("Hayır"){ dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()

        }
    }

    fun showParticipant(view: View) {
        view.isEnabled = false
        progressCircle.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.rv_participants).layoutManager = layoutManager
        db.collection("users").get().addOnSuccessListener { snapshot ->
            if (!snapshot.isEmpty) {
                val documents = snapshot.documents
                for (mail in participantsMail) {
                    for (userInfo in documents) {
                        if (userInfo.id == mail) {
                            participantsName.add(userInfo["name"] as String)
                            participantsSurname.add(userInfo["surname"] as String)
                            break
                        }
                    }
                }
                progressCircle.visibility = View.INVISIBLE
                val adapter = RecyclerAdapterParticipants(this, participantsName, participantsSurname,participantsMail,eventID)
                findViewById<RecyclerView>(R.id.rv_participants).adapter = adapter
            }
        }
    }
}
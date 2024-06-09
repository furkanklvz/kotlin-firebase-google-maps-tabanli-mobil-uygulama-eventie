package com.example.gezirehberi

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class JoinedEventDescriptionActivity : AppCompatActivity() {
    val db = Firebase.firestore
    val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_joined_event_description)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val intent = intent
        val eventID = intent.getStringExtra("eventID")
        val eventDate = intent.getStringExtra("eventDate")
        db.collection("events").document(eventID!!).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                findViewById<TextView>(R.id.tv_eventTitle).text = snapshot["eventTitle"] as String
                findViewById<TextView>(R.id.tv_eventDate).text = eventDate
                findViewById<TextView>(R.id.tv_eventAdress).text = snapshot["location"] as String
                findViewById<TextView>(R.id.tv_eventDescription).text =
                    snapshot["eventDetails"] as String
                val participantLimit = (snapshot["participantLimit"] as Number).toInt()
                val price = (snapshot["price"] as Number).toInt()
                findViewById<TextView>(R.id.tv_participantLimit).text =
                    if (participantLimit == 99999) "Sınırsız" else participantLimit.toString()
                findViewById<TextView>(R.id.tv_eventPrice).text =
                    if (price == 0) "Ücretsiz" else price.toString()+" TL"
                findViewById<TextView>(R.id.tv_eventCategory).text = snapshot["category"] as String
            }
        }

        findViewById<Button>(R.id.bt_leaveEvent).setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Etkinlikten Ayrıl?")
            builder.setMessage("Etkinlikten ayrılmak istediğinize emin misiniz?")
            builder.setPositiveButton("Evet"){ dialog, which ->
                db.collection("users").document(auth.currentUser?.email.toString()).get().addOnSuccessListener { snapshot->
                    if(snapshot.exists()){
                        val eventList = snapshot["joinedEvents"] as ArrayList<String>
                        eventList.removeAt(eventList.indexOf(eventID))
                        db.collection("users").document(auth.currentUser?.email.toString()).update("joinedEvents",eventList)
                        db.collection("events").document(eventID).get().addOnSuccessListener { snapshot->
                            if(snapshot.exists()){
                                val participantList = snapshot["participants"] as ArrayList<String>
                                participantList.removeAt(participantList.indexOf(auth.currentUser?.email.toString()))
                                db.collection("events").document(eventID).update("participants",participantList)
                                val intent = Intent(this,HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }.addOnFailureListener { e->
                            Toast.makeText(this,e.localizedMessage,Toast.LENGTH_SHORT).show()
                            return@addOnFailureListener
                        }
                    }
                }.addOnFailureListener { e->
                    Toast.makeText(this,e.localizedMessage,Toast.LENGTH_SHORT).show()
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
}
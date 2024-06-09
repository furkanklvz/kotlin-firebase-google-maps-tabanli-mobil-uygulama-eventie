package com.example.gezirehberi

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class ViewTheEventActivity : AppCompatActivity() {
    val auth = Firebase.auth
    val db = Firebase.firestore
    lateinit var clickedEventID: String
    var eventIsFull : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_the_event)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val intent = intent
        clickedEventID = intent.getStringExtra("eventID")!!
        getDatas()


    }

    private fun getDatas() {
        db.collection("events").document(clickedEventID).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val numOfJoinedParticipants = if (snapshot.contains("participants")){
                    (snapshot["participants"] as MutableList<String>).size
                }else 0
                findViewById<TextView>(R.id.tv_eventTitlev).text = snapshot["eventTitle"] as String
                val date = SimpleDateFormat("dd/MM/yyyy",
                    Locale.getDefault()).format((snapshot["eventDate"] as Timestamp).toDate())
                findViewById<TextView>(R.id.tv_eventDatev).text = date
                findViewById<TextView>(R.id.tv_eventAdressv).text = snapshot["location"] as String
                findViewById<TextView>(R.id.tv_eventDescriptionv).text =
                    snapshot["eventDetails"] as String
                val participantLimit = (snapshot["participantLimit"] as Number).toInt()
                if (participantLimit-numOfJoinedParticipants == 0) eventIsFull = true
                val price = (snapshot["price"] as Number).toInt()
                findViewById<TextView>(R.id.tv_participantLimitv).text =
                    if (participantLimit == 99999) "Sınırsız" else numOfJoinedParticipants.toString() +"/"+participantLimit.toString()
                findViewById<TextView>(R.id.tv_eventPricev).text =
                    if (price == 0) "Ücretsiz" else price.toString()
                findViewById<TextView>(R.id.tv_eventCategoryv).text = snapshot["category"] as String
            }
        }
    }

    fun etkinligeKatil(view: View) {
        if (eventIsFull){
            Toast.makeText(applicationContext,"Bu etkinlik katılımcı sınırına ulaşmış",Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(auth.currentUser!!.email!!).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    if (snapshot.exists()) {
                        val katilinanEtkinlikler: MutableList<String> =
                            if (snapshot.contains("joinedEvents")) {
                                snapshot["joinedEvents"] as MutableList<String>
                            } else {
                                mutableListOf()
                            }
                        if (katilinanEtkinlikler.contains(clickedEventID)) {
                            Toast.makeText(
                                applicationContext,
                                "Bu etkinliğe zaten katıldın.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            katilinanEtkinlikler.add(clickedEventID)
                            val updatedData = hashMapOf(
                                "joinedEvents" to katilinanEtkinlikler
                            )
                            db.collection("users").document(snapshot.id).update(updatedData.toMap())
                                .addOnSuccessListener { DocumentReference ->
                                    Toast.makeText(
                                        applicationContext,
                                        "Etkinliğe başarıyla katılınıldı",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    view.isEnabled = false
                                }.addOnFailureListener { e ->
                                    Toast.makeText(
                                        applicationContext,
                                        "Hata: " + e.localizedMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            db.collection("events").document(clickedEventID).get()
                                .addOnSuccessListener { eventsSnapshot ->
                                    if (eventsSnapshot.exists()) {
                                        val joiners: MutableList<String> =
                                            if (eventsSnapshot.contains("participants")) {
                                                eventsSnapshot["participants"] as MutableList<String>
                                            } else {
                                                mutableListOf()
                                            }
                                        joiners.add(auth.currentUser!!.email!!)
                                        val updatedJoiners = hashMapOf(
                                            "participants" to joiners
                                        )
                                        db.collection("events").document(clickedEventID)
                                            .update(updatedJoiners.toMap())
                                            .addOnSuccessListener { DocumentReference ->
                                            }.addOnFailureListener { e ->
                                                Toast.makeText(
                                                    applicationContext,
                                                    "Etkinliğe katılım sağlanamadı: " + e.localizedMessage,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                }
                        }
                    }
                }
            }
    }
}
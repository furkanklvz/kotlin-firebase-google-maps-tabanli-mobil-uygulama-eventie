package com.example.gezirehberi

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gezirehberi.adapter.RecyclerAdapterEventOwner
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class CreatedEventsActivity : AppCompatActivity() {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val events: MutableList<String> = mutableListOf()
    val eventsDates: MutableList<Date> = mutableListOf()
    val eventsIDs: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_created_events)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.recyclerView2).layoutManager = layoutManager
        db.collection("events").whereEqualTo("creator", auth.currentUser!!.email).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val documents = snapshot.documents

                    for (document in documents) {
                        events.add(document["eventTitle"] as String)
                        eventsDates.add((document["eventDate"] as Timestamp).toDate())
                        eventsIDs.add(document["eventID"] as String)
                    }
                    findViewById<ProgressBar>(R.id.pb_createdEvents).visibility = View.INVISIBLE
                    val adapter = RecyclerAdapterEventOwner(events, eventsDates,eventsIDs)
                    findViewById<RecyclerView>(R.id.recyclerView2).adapter = adapter
                }
                else{
                    findViewById<ProgressBar>(R.id.pb_createdEvents).visibility = View.INVISIBLE
                    Toast.makeText(applicationContext,"Henüz bir etkinlik oluşturmadınız",Toast.LENGTH_SHORT).show()
                    finish()
                }
            }


    }

}
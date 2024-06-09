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
import com.example.gezirehberi.adapter.RecyclerAdapter
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class JoinedEventsActivity : AppCompatActivity() {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val joinedEventsTitle: MutableList<String> = mutableListOf()
    val joinedEventsDates: MutableList<Date> = mutableListOf()
    val joinedEventsIDs: MutableList<String> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_joined_events)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val layoutManager = LinearLayoutManager(this)
        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = layoutManager
        db.collection("users").document(auth.currentUser!!.email!!).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()){
                    if (snapshot.contains("joinedEvents")) {
                        val joinedEvents: MutableList<String> =
                            snapshot["joinedEvents"] as MutableList<String>
                        if (joinedEvents.isEmpty()){
                            findViewById<ProgressBar>(R.id.pb_joinedEvents).visibility = View.INVISIBLE
                            Toast.makeText(applicationContext,"Henüz bir etkinliğe katılmadınız",
                                Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        db.collection("events").get().addOnSuccessListener { snapshot ->
                            if (!snapshot.isEmpty) {
                                val events = snapshot.documents
                                for (eventID in joinedEvents) {
                                    for (event in events) {
                                        if (eventID == event.id) {
                                            joinedEventsTitle.add(event["eventTitle"] as String)
                                            joinedEventsDates.add((event["eventDate"] as Timestamp).toDate())
                                            joinedEventsIDs.add(eventID)
                                            break
                                        }
                                    }
                                }
                                findViewById<ProgressBar>(R.id.pb_joinedEvents).visibility =
                                    View.INVISIBLE
                                val adapter = RecyclerAdapter(
                                    joinedEventsTitle,
                                    joinedEventsDates,
                                    joinedEventsIDs
                                )
                                findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
                            }
                        }
                    }else{
                        findViewById<ProgressBar>(R.id.pb_joinedEvents).visibility = View.INVISIBLE
                        Toast.makeText(applicationContext,"Henüz bir etkinliğe katılmadınız",
                            Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
    }
}

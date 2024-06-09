package com.example.gezirehberi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.app.AlertDialog
import android.content.DialogInterface

class SignUpActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var eMailKayit: EditText
    lateinit var kullaniciAdiKayit: EditText
    lateinit var adKayit: EditText
    lateinit var soyadKayit: EditText
    lateinit var telKayit: EditText
    lateinit var sifreKayit: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayout2)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adKayit = findViewById(R.id.et_adKayit)
        soyadKayit = findViewById(R.id.et_soyadKayit)
        telKayit = findViewById(R.id.et_telKayit)
        eMailKayit = findViewById(R.id.et_eMailKayit)
        kullaniciAdiKayit = findViewById(R.id.et_kullaniciAdiKayit)
        sifreKayit = findViewById(R.id.et_sifreKayit)
        auth = Firebase.auth
    }

    fun kayitOl(view: View) {
        if (kullaniciAdiKayit.text.toString() != "" && adKayit.text.toString() != "" && soyadKayit.text.toString() != ""
            && telKayit.text.toString() != ""
        ) {
            auth.createUserWithEmailAndPassword(
                eMailKayit.text.toString(),
                sifreKayit.text.toString()
            ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {


                        val user = hashMapOf(
                            "name" to adKayit.text.toString(),
                            "surname" to soyadKayit.text.toString(),
                            "username" to kullaniciAdiKayit.text.toString(),
                            "tel" to telKayit.text.toString(),
                            "eMail" to eMailKayit.text.toString(),
                            "password" to sifreKayit.text.toString()
                        )
                        db.collection("users").document(eMailKayit.text.toString()).set(user)
                            .addOnSuccessListener { documentReference ->
                                val profileUpdates = userProfileChangeRequest {
                                    displayName = kullaniciAdiKayit.text.toString()
                                }
                                auth.currentUser?.updateProfile(profileUpdates)!!.addOnSuccessListener {
                                    Toast.makeText(
                                        applicationContext,
                                        "Kullanıcı Başarıyla kaydedildi.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    finish()
                                }

                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    applicationContext,
                                    e.localizedMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }.addOnFailureListener(this) { exception ->
                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }

        } else {
            Toast.makeText(applicationContext, "Hiçbir alan boş bırakılamaz!", Toast.LENGTH_LONG)
                .show()
        }
    }
}
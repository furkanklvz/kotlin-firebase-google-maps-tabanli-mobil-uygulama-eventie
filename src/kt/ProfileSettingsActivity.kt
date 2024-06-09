package com.example.gezirehberi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore

class ProfileSettingsActivity : AppCompatActivity() {
    val db = Firebase.firestore
    val auth = Firebase.auth
    lateinit var pb_loading: ProgressBar
    lateinit var bt_edit_username: ImageButton
    lateinit var bt_edit_name: ImageButton
    lateinit var bt_edit_surname: ImageButton
    //lateinit var bt_edit_email: ImageButton
    lateinit var bt_edit_tel: ImageButton
    lateinit var bt_save_username: ImageButton
    lateinit var bt_save_name: ImageButton
    lateinit var bt_save_surname: ImageButton
    //lateinit var bt_save_email: ImageButton
    lateinit var bt_save_tel: ImageButton

    lateinit var et_edit_username: EditText
    lateinit var et_edit_name: EditText
    lateinit var et_edit_surname: EditText
    //lateinit var et_edit_email: EditText
    lateinit var et_edit_tel: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val user_mail = intent.getStringExtra("user_mail")
        pb_loading = findViewById(R.id.pb_profile_settings)
        pb_loading.isEnabled = true
        pb_loading.visibility = View.VISIBLE
        bt_edit_username = findViewById(R.id.bt_edit_username)
        bt_edit_name = findViewById(R.id.bt_edit_name)
        bt_edit_surname = findViewById(R.id.bt_edit_surname)
        //bt_edit_email = findViewById(R.id.bt_edit_email)
        bt_edit_tel = findViewById(R.id.bt_edit_tel)

        bt_save_username = findViewById(R.id.bt_check_username)
        bt_save_name = findViewById(R.id.bt_check_name)
        bt_save_surname = findViewById(R.id.bt_check_surname)
        //bt_save_email = findViewById(R.id.bt_check_email)
        bt_save_tel = findViewById(R.id.bt_check_tel)

        et_edit_username = findViewById(R.id.et_edit_username)
        et_edit_name = findViewById(R.id.et_edit_name)
        et_edit_surname = findViewById(R.id.et_edit_surname)
        //et_edit_email = findViewById(R.id.et_edit_email)
        et_edit_tel = findViewById(R.id.et_edit_tel)

        et_edit_username.isEnabled = false
        et_edit_name.isEnabled = false
        et_edit_surname.isEnabled = false
        //et_edit_email.isEnabled = false
        et_edit_tel.isEnabled = false

        bt_save_username.visibility = View.INVISIBLE
        bt_save_name.visibility = View.INVISIBLE
        bt_save_surname.visibility = View.INVISIBLE
        //bt_save_email.visibility = View.INVISIBLE
        bt_save_tel.visibility = View.INVISIBLE
        bt_save_username.isEnabled = false
        bt_save_name.isEnabled = false
        bt_save_surname.isEnabled = false
        //bt_save_email.isEnabled = false
        bt_save_tel.isEnabled = false

        findViewById<Button>(R.id.bt_ps_reset_password).setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            intent.putExtra("sourceActivity", "ProfileSettingsActivity")
            startActivity(intent)
        }

        db.collection("users").document(user_mail!!).get().addOnSuccessListener {
            pb_loading.isEnabled = false
            pb_loading.visibility = View.INVISIBLE
            et_edit_username.setText(auth.currentUser?.displayName.toString())
            et_edit_name.setText(it.get("name").toString())
            et_edit_surname.setText(it.get("surname").toString())
            //et_edit_email.setText(auth.currentUser?.email)
            et_edit_tel.setText(it.get("tel").toString())
        }

        bt_edit_username.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            bt_save_username.visibility = View.VISIBLE
            bt_save_username.isEnabled = true
            et_edit_username.isEnabled = true
        }
        bt_edit_name.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            bt_save_name.visibility = View.VISIBLE
            bt_save_name.isEnabled = true
            et_edit_name.isEnabled = true
        }
        bt_edit_surname.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            bt_save_surname.visibility = View.VISIBLE
            bt_save_surname.isEnabled = true
            et_edit_surname.isEnabled = true
        }
        /*bt_edit_email.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            bt_save_email.visibility = View.VISIBLE
            bt_save_email.isEnabled = true
            et_edit_email.isEnabled = true
        }*/
        bt_edit_tel.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            bt_save_tel.visibility = View.VISIBLE
            bt_save_tel.isEnabled = true
            et_edit_tel.isEnabled = true
        }
        bt_save_username.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            db.collection("users").document(user_mail!!)
                .update("username", et_edit_username.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = et_edit_username.text.toString()
                        }
                        auth.currentUser?.updateProfile(profileUpdates)?.addOnSuccessListener {
                            Toast.makeText(
                                applicationContext,
                                "Kullanıcı Adı Güncellendi",
                                Toast.LENGTH_SHORT
                            ).show()
                            bt_edit_username.visibility = View.VISIBLE
                            bt_edit_username.isEnabled = true
                            et_edit_username.isEnabled = false
                        }

                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    bt_edit_username.visibility = View.VISIBLE
                    bt_edit_username.isEnabled = true
                }

        }
        bt_save_name.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            db.collection("users").document(user_mail!!)
                .update("name", et_edit_name.text.toString()).addOnSuccessListener {
                    Toast.makeText(applicationContext, "İsim Güncellendi", Toast.LENGTH_SHORT)
                        .show()
                    bt_edit_name.visibility = View.VISIBLE
                    bt_edit_name.isEnabled = true
                    et_edit_name.isEnabled = false
                }.addOnFailureListener { e ->
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    bt_edit_name.visibility = View.VISIBLE
                    bt_edit_name.isEnabled = true
                }
        }
        bt_save_surname.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            db.collection("users").document(user_mail!!)
                .update("surname", et_edit_surname.text.toString()).addOnSuccessListener {
                    Toast.makeText(applicationContext, "Soyisim Güncellendi", Toast.LENGTH_SHORT)
                        .show()
                    bt_edit_surname.visibility = View.VISIBLE
                    bt_edit_surname.isEnabled = true
                    et_edit_surname.isEnabled = false
                }.addOnFailureListener { e ->
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    bt_edit_surname.visibility = View.VISIBLE
                    bt_edit_surname.isEnabled = true
                }
        }
        /*bt_save_email.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            db.collection("users").document(user_mail!!)
                .update("email", et_edit_email.text.toString()).addOnSuccessListener {
                    auth.currentUser?.updateEmail(et_edit_email.text.toString())
                        ?.addOnSuccessListener {
                            Toast.makeText(
                                applicationContext,
                                "Email Güncellendi",
                                Toast.LENGTH_SHORT
                            ).show()
                            bt_edit_email.visibility = View.VISIBLE
                            bt_edit_email.isEnabled = true
                            et_edit_email.isEnabled = false
                        }
                }.addOnFailureListener {e->
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    bt_edit_email.visibility = View.VISIBLE
                    bt_edit_email.isEnabled = true
                }
        }*/
        bt_save_tel.setOnClickListener {
            it.isEnabled = false
            it.visibility = View.INVISIBLE
            db.collection("users").document(user_mail!!).update("tel", et_edit_tel.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Telefon Güncellendi", Toast.LENGTH_SHORT)
                        .show()
                    bt_edit_tel.visibility = View.VISIBLE
                    bt_edit_tel.isEnabled = true
                    et_edit_tel.isEnabled = false
                }.addOnFailureListener { e ->
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    bt_edit_tel.visibility = View.VISIBLE
                    bt_edit_tel.isEnabled = true
                }
        }

    }
}
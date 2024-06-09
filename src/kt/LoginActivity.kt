package com.example.gezirehberi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    val auth = Firebase.auth
    lateinit var eMail : EditText
    lateinit var sifre : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        eMail = findViewById(R.id.et_eMail)
        sifre = findViewById(R.id.et_sifre)

        if (auth.currentUser != null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun girisYap(view : View){
        auth.signInWithEmailAndPassword(eMail.text.toString(), sifre.text.toString()).addOnCompleteListener { task->
            if(task.isSuccessful){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener { exception->
            Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
    fun kayitOlButon(view: View){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
    fun forgotMyPassword(view: View){
        val intent = Intent(this,ResetPasswordActivity::class.java)
        intent.putExtra("sourceActivity", "LoginActivity")
        startActivity(intent)
    }
}
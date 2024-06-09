package com.example.gezirehberi

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val sourceActivity = intent.getStringExtra("sourceActivity")
        if (sourceActivity == "ProfileSettingsActivity"){
            findViewById<EditText>(R.id.et_eMail_resetPassword).setText(Firebase.auth.currentUser?.email)
        }
    }

    fun sendResetLink(view: View) {
        if (findViewById<EditText>(R.id.et_eMail_resetPassword).text.toString().trim().isEmpty()){
            Toast.makeText(this, "Geçerli bir e-mail giriniz",Toast.LENGTH_SHORT).show()
            return
        }
        Firebase.auth.sendPasswordResetEmail(findViewById<EditText>(R.id.et_eMail_resetPassword).text.toString()).addOnCompleteListener { task->
            if (task.isSuccessful){
                view.isEnabled = false
                (view as Button).text = "E-Posta Gönderildi"
            }else{
                Toast.makeText(this,"Hata oluştu. Tekrar Deneyin",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
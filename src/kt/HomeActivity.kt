package com.example.gezirehberi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.app.AlertDialog

class HomeActivity : AppCompatActivity() {

    lateinit var  auth : FirebaseAuth
    lateinit var user_mail : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        user_mail = auth.currentUser?.email.toString()
        findViewById<TextView>(R.id.tv_hosgeldin).text = "Hoşgeldin ${auth.currentUser?.displayName.toString()}"
        /*val textView = findViewById<TextView>(R.id.title)
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())
        val textShader = LinearGradient(
            0f, 0f, width, textView.textSize,
            intArrayOf(
                /*0xFFE91E63.toInt(),  // pink
                0xFFFFC107.toInt(),  // amber
                0xFF4CAF50.toInt(),  // green*/
                0xFF00BCD4.toInt(),  // blue
                0xFF9C27B0.toInt()   // purple
            ),
            null,
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = textShader*/
    }
    fun haritalaraGit(view: View){

        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
    fun katildigimEtkinlikler(view: View){

        val intent = Intent(this, JoinedEventsActivity::class.java)
        startActivity(intent)
    }
    fun duzenledigimEtkinlikler(view: View){

        val intent = Intent(this, CreatedEventsActivity::class.java)
        startActivity(intent)
    }

    fun signOut(view: View) {

        // AlertDialog.Builder kullanarak bir AlertDialog oluşturun
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Çıkış Yap")
        builder.setMessage("Çıkış yapmak istediğinize emin misiniz?")

        // Pozitif buton ve tıklama dinleyicisi ekleyin
        builder.setPositiveButton("Evet") { dialog, _ ->
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Negatif buton ve tıklama dinleyicisi ekleyin (isteğe bağlı)
        builder.setNegativeButton("İptal") { dialog, _ ->
            dialog.dismiss()
        }

        // Dialog'u oluşturun ve gösterin
        val dialog = builder.create()
        dialog.show()
    }
    fun goToProfile(view: View){
        val intent = Intent(this, ProfileSettingsActivity::class.java)
        intent.putExtra("user_mail", user_mail)
        startActivity(intent)
    }
}
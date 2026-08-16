package com.udb.appfinanzas.auth.ui
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.udb.appfinanzas.R
import com.udb.appfinanzas.dashboard.ui.ActividadDashboard

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        findViewById<Button>(R.id.btnRegistrar).setOnClickListener { startActivity(Intent(this,
            ActividadDashboard::class.java))
        }
    }
}
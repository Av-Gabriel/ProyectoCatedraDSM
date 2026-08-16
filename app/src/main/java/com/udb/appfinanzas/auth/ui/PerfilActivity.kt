package com.udb.appfinanzas.auth.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.udb.appfinanzas.R

class PerfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        findViewById<Button>(R.id.btnCerrarSesion).setOnClickListener {
            //se usara firebase a futuro
            val intent = Intent(this, LoginActivity::class.java)
            // limpiar el historial de pantallas para que no se pueda regresar a dashboar si se cerro la sesion
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
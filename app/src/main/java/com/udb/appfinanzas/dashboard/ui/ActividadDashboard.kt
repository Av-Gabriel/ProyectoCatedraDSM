package com.udb.appfinanzas.dashboard.ui
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.udb.appfinanzas.R
import com.udb.appfinanzas.auth.ui.PerfilActivity
import com.udb.appfinanzas.historial.ui.HistorialActivity
import com.udb.appfinanzas.movimientos.ui.AgregarMovimiento
import com.udb.appfinanzas.noticias.ui.NoticiasActivity
import com.udb.appfinanzas.presupuesto.ui.PresupuestosActivity

class ActividadDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        findViewById<Button>(R.id.btnAgregarMovimiento).setOnClickListener { startActivity(Intent(this,
            AgregarMovimiento::class.java)) }
        findViewById<Button>(R.id.btnPresupuestos).setOnClickListener { startActivity(Intent(this,
            PresupuestosActivity::class.java)) }
        findViewById<Button>(R.id.btnHistorial).setOnClickListener { startActivity(Intent(this,
            HistorialActivity::class.java)) }
        findViewById<Button>(R.id.btnNoticias).setOnClickListener { startActivity(Intent(this,
            NoticiasActivity::class.java))
        }
        findViewById<Button>(R.id.btnPerfil).setOnClickListener { startActivity(Intent(this,
            PerfilActivity::class.java)) }
    }
}
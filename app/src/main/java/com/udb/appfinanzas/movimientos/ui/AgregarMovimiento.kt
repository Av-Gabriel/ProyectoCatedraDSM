package com.udb.appfinanzas.movimientos.ui
import com.udb.appfinanzas.R
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class AgregarMovimiento : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_movimiento)

        val categorias = arrayOf("Alimentacion", "Transporte", "Ocio", "Servicios","Salario")
        val spCategoria = findViewById<Spinner>(R.id.spCategoria)
        spCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categorias)

        findViewById<Button>(R.id.btnGuardarMovimiento).setOnClickListener {

            finish()
        }
    }
}
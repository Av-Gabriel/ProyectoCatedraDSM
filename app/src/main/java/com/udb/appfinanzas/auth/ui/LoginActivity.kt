import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.udb.appfinanzas.R
import com.udb.appfinanzas.dashboard.ui.ActividadDashboard
import org.w3c.dom.Text


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //se conecta las variables kotlin con el XML

        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val tvItRegistro = findViewById<TextView>(R.id.tvIrRegistro)

        //accion al tocar el boton ingresar

        btnIngresar.setOnClickListener { //de momento nada hasta que se haga el backend
            val intent = Intent(this, ActividadDashboard::class.java)
            startActivity(intent)


        }

    }
}
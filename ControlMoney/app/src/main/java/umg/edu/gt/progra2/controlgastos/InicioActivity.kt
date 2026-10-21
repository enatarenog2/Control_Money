package umg.edu.gt.progra2.controlgastos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import umg.edu.gt.progra2.controlgastos.databinding.ActivityInicioBinding

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrarMovimiento.setOnClickListener {
            startActivity(
                Intent(this, RegistrarMovimientoActivity::class.java)
            )
        }

        binding.btnVerMovimientos.setOnClickListener {
            startActivity(
                Intent(this, HistorialActivity::class.java)
            )
        }

        binding.btnReportes.setOnClickListener {
            startActivity(
                Intent(this, ReportesActivity::class.java)
            )
        }

        binding.btnPerfil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("editar_datos", true)
            startActivity(intent)
        }

    }
}

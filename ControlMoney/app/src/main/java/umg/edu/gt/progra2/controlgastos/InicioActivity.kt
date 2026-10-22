package umg.edu.gt.progra2.controlgastos

import android.content.Intent
import android.os.Bundle
import umg.edu.gt.progra2.controlgastos.databinding.ActivityInicioBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale

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

    override fun onResume() {
        super.onResume()
        actualizarResumen()
    }

    private fun actualizarResumen() {
        lifecycleScope.launch {
            try {
                val control = ControlGastosProvider.instancia

                val ingresos = control.calcularTotalIngresos()
                val gastos = control.calcularTotalGastos()
                val saldo = control.calcularSaldo()

                binding.tvTotalIngresos.text = formatoQuetzales(ingresos)
                binding.tvTotalGastos.text = formatoQuetzales(gastos)
                binding.tvSaldoDisponible.text = formatoQuetzales(saldo)

            } catch (e: Exception) {
                android.widget.Toast.makeText(
                    this@InicioActivity,
                    "No se pudo actualizar el resumen financiero",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun formatoQuetzales(monto: Double): String {
        return String.format(Locale.US, "Q %,.2f", monto)
    }

}

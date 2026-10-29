package umg.edu.gt.progra2.controlgastos

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.progra2.controlgastos.databinding.ActivityInicioBinding
import java.util.Locale

class InicioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInicioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botón para registrar un ingreso o gasto
        binding.btnRegistrarMovimiento.setOnClickListener {
            startActivity(
                Intent(this, RegistrarMovimientoActivity::class.java)
            )
        }

        // Navegación al historial
        binding.btnVerMovimientos.setOnClickListener {
            startActivity(
                Intent(this, HistorialActivity::class.java)
            )
        }

        // Navegación a reportes
        binding.btnReportes.setOnClickListener {
            startActivity(
                Intent(this, ReportesActivity::class.java)
            )
        }

        // Abrir los datos del usuario
        binding.btnPerfil.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("editar_datos", true)
            startActivity(intent)
        }

        // Barra de navegación inferior
        binding.navInicio.setOnClickListener {
            // Ya estamos en Inicio.
        }

        binding.navHistorial.setOnClickListener {
            startActivity(
                Intent(this, HistorialActivity::class.java)
            )
        }

        binding.navReportes.setOnClickListener {
            startActivity(
                Intent(this, ReportesActivity::class.java)
            )
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

                binding.tvTotalIngresos.text =
                    formatoQuetzales(ingresos)

                binding.tvTotalGastos.text =
                    formatoQuetzales(gastos)

                binding.tvSaldoDisponible.text =
                    formatoQuetzales(saldo)

            } catch (e: Exception) {
                Toast.makeText(
                    this@InicioActivity,
                    "No se pudo actualizar el resumen financiero",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun formatoQuetzales(monto: Double): String {
        return String.format(Locale.US, "Q %,.2f", monto)
    }
}
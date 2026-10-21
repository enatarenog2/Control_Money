package umg.edu.gt.progra2.controlgastos

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.progra2.controlgastos.databinding.ActivityRegistrarMovimientoBinding
import umg.edu.gt.progra2.controlgastos.modelo.Categoria
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class RegistrarMovimientoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarMovimientoBinding

    private var fechaSeleccionada = LocalDate.now()

    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val tiposMovimiento = listOf("Gasto", "Ingreso")

    private val categorias = listOf(
        "Alimentos",
        "Transporte",
        "Servicios",
        "Entretenimiento",
        "Otros"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarMovimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarDesplegables()
        configurarFecha()
        configurarBotones()
    }

    private fun configurarDesplegables() {
        val adaptadorTipos = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            tiposMovimiento
        )
        adaptadorTipos.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spTipoMovimiento.adapter = adaptadorTipos

        val adaptadorCategorias = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )
        adaptadorCategorias.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spCategoriaMovimiento.adapter = adaptadorCategorias
    }

    private fun configurarFecha() {
        binding.btnFechaMovimiento.text =
            fechaSeleccionada.format(formatoFecha)

        binding.btnFechaMovimiento.setOnClickListener {
            val calendario = Calendar.getInstance().apply {
                set(
                    fechaSeleccionada.year,
                    fechaSeleccionada.monthValue - 1,
                    fechaSeleccionada.dayOfMonth
                )
            }

            val selector = DatePickerDialog(
                this,
                { _, anio, mes, dia ->
                    fechaSeleccionada = LocalDate.of(anio, mes + 1, dia)
                    binding.btnFechaMovimiento.text =
                        fechaSeleccionada.format(formatoFecha)
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )

            selector.datePicker.maxDate = System.currentTimeMillis()
            selector.show()
        }
    }

    private fun configurarBotones() {
        binding.btnGuardarMovimiento.setOnClickListener {
            guardarMovimiento()
        }

        binding.btnCancelarMovimiento.setOnClickListener {
            finish()
        }
    }

    private fun guardarMovimiento() {
        val descripcion = binding.etDescripcionMovimiento.text
            .toString().trim()

        val textoMonto = binding.etMontoMovimiento.text
            .toString().trim()

        val tipo = binding.spTipoMovimiento.selectedItem.toString()

        if (descripcion.isEmpty()) {
            binding.etDescripcionMovimiento.error =
                "Ingresa una descripción"
            binding.etDescripcionMovimiento.requestFocus()
            return
        }

        val monto = textoMonto.toDoubleOrNull()

        if (monto == null || monto <= 0.0) {
            binding.etMontoMovimiento.error =
                "Ingresa un monto mayor que cero"
            binding.etMontoMovimiento.requestFocus()
            return
        }

        val nombreCategoria =
            binding.spCategoriaMovimiento.selectedItem.toString()

        val categoria = when (nombreCategoria) {
            "Alimentos" -> Categoria.ALIMENTOS
            "Transporte" -> Categoria.TRANSPORTE
            "Servicios" -> Categoria.SERVICIOS
            "Entretenimiento" -> Categoria.ENTRETENIMIENTO
            else -> Categoria.OTROS
        }

        binding.btnGuardarMovimiento.isEnabled = false

        lifecycleScope.launch {
            try {
                if (tipo == "Ingreso") {
                    ControlGastosProvider.instancia.registrarIngreso(
                        descripcion = descripcion,
                        monto = monto,
                        categoria = categoria,
                        fecha = fechaSeleccionada
                    )
                } else {
                    ControlGastosProvider.instancia.registrarGasto(
                        descripcion = descripcion,
                        monto = monto,
                        categoria = categoria,
                        fecha = fechaSeleccionada
                    )
                }

                Toast.makeText(
                    this@RegistrarMovimientoActivity,
                    "$tipo registrado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@RegistrarMovimientoActivity,
                    "No se pudo registrar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                binding.btnGuardarMovimiento.isEnabled = true
            }
        }
    }
}

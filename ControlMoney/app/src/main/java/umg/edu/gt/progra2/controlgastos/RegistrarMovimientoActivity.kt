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
import androidx.appcompat.app.AlertDialog
import umg.edu.gt.progra2.controlgastos.modelo.TipoMovimiento
import android.widget.AdapterView

class RegistrarMovimientoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarMovimientoBinding

    private var fechaSeleccionada = LocalDate.now()

    private var movimientoId: Int = -1

    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    private val tiposMovimiento = listOf("Gasto", "Ingreso")

    private val categorias = listOf(
        "Alimentos",
        "Transporte",
        "Servicios",
        "Entretenimiento",
        "Otros"
    )
    private var mostrandoNoAplica = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarMovimientoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarDesplegables()
        configurarFecha()
        configurarBotones()

        movimientoId = intent.getIntExtra("movimiento_id", -1)

        if (movimientoId != -1) {
            binding.btnEliminarMovimiento.visibility = android.view.View.VISIBLE
            cargarMovimiento()
        } else {
            binding.btnEliminarMovimiento.visibility = android.view.View.GONE
        }
    }
    private fun cargarMovimiento() {
        lifecycleScope.launch {
            try {
                val movimiento = ControlGastosProvider.instancia
                    .buscarPorId(movimientoId)

                if (movimiento == null) {
                    Toast.makeText(
                        this@RegistrarMovimientoActivity,
                        "No se encontró el movimiento",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                    return@launch
                }

                binding.etDescripcionMovimiento.setText(
                    movimiento.descripcion
                )

                binding.etMontoMovimiento.setText(
                    movimiento.monto.toString()
                )

                val tipoTexto = if (
                    movimiento.tipo == TipoMovimiento.INGRESO
                ) "Ingreso" else "Gasto"

                val posicionTipo = tiposMovimiento.indexOf(tipoTexto)
                if (posicionTipo >= 0) {
                    binding.spTipoMovimiento.setSelection(posicionTipo)
                }

                val categoriaTexto = when (movimiento.categoria) {
                    Categoria.ALIMENTOS -> "Alimentos"
                    Categoria.TRANSPORTE -> "Transporte"
                    Categoria.SERVICIOS -> "Servicios"
                    Categoria.ENTRETENIMIENTO -> "Entretenimiento"
                    Categoria.OTROS -> "Otros"
                }

                val posicionCategoria = categorias.indexOf(categoriaTexto)
                if (posicionCategoria >= 0) {
                    binding.spCategoriaMovimiento.setSelection(
                        posicionCategoria
                    )
                }

                fechaSeleccionada = movimiento.fecha

                binding.btnFechaMovimiento.text =
                    fechaSeleccionada.format(formatoFecha)

            } catch (e: Exception) {
                Toast.makeText(
                    this@RegistrarMovimientoActivity,
                    "No se pudo cargar el movimiento: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

                finish()
            }
        }
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

        actualizarEstadoCategoria()

        binding.spTipoMovimiento.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    actualizarEstadoCategoria()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }
    private fun actualizarEstadoCategoria() {

        val tipoSeleccionado =
            binding.spTipoMovimiento.selectedItem.toString()

        if (tipoSeleccionado == "Ingreso") {

            if (!mostrandoNoAplica) {
                val adaptador = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    listOf("No aplica")
                )

                adaptador.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                binding.spCategoriaMovimiento.adapter = adaptador
                mostrandoNoAplica = true
            }

            binding.spCategoriaMovimiento.isEnabled = false
            binding.spCategoriaMovimiento.alpha = 0.5f

        } else {

            if (mostrandoNoAplica) {
                val adaptador = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    categorias
                )

                adaptador.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                binding.spCategoriaMovimiento.adapter = adaptador
                mostrandoNoAplica = false
            }

            binding.spCategoriaMovimiento.isEnabled = true
            binding.spCategoriaMovimiento.alpha = 1f
        }
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
        binding.btnEliminarMovimiento.setOnClickListener {
            confirmarEliminacion()
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

        val categoria = if (tipo == "Ingreso") {
            Categoria.OTROS
        } else {
            val nombreCategoria =
                binding.spCategoriaMovimiento.selectedItem.toString()

            when (nombreCategoria) {
                "Alimentos" -> Categoria.ALIMENTOS
                "Transporte" -> Categoria.TRANSPORTE
                "Servicios" -> Categoria.SERVICIOS
                "Entretenimiento" -> Categoria.ENTRETENIMIENTO
                else -> Categoria.OTROS
            }
        }

        binding.btnGuardarMovimiento.isEnabled = false

        lifecycleScope.launch {
            try {
                val control = ControlGastosProvider.instancia

                if (movimientoId != -1) {
                    // Actualizar el movimiento existente.
                    val movimientoActual = control.buscarPorId(movimientoId)

                    if (movimientoActual == null) {
                        Toast.makeText(
                            this@RegistrarMovimientoActivity,
                            "No se encontró el movimiento",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    val movimientoActualizado = movimientoActual.copy(
                        descripcion = descripcion,
                        monto = monto,
                        categoria = categoria,
                        fecha = fechaSeleccionada,
                        tipo = if (tipo == "Ingreso") {
                            TipoMovimiento.INGRESO
                        } else {
                            TipoMovimiento.GASTO
                        }
                    )

                    val actualizado = control.actualizar(
                        movimientoActualizado
                    )

                    if (!actualizado) {
                        Toast.makeText(
                            this@RegistrarMovimientoActivity,
                            "No se pudieron guardar los cambios",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    Toast.makeText(
                        this@RegistrarMovimientoActivity,
                        "Movimiento actualizado correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    // Registrar un movimiento nuevo.
                    if (tipo == "Ingreso") {
                        control.registrarIngreso(
                            descripcion = descripcion,
                            monto = monto,
                            categoria = categoria,
                            fecha = fechaSeleccionada
                        )
                    } else {
                        control.registrarGasto(
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
                }

                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@RegistrarMovimientoActivity,
                    "No se pudo guardar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnGuardarMovimiento.isEnabled = true
            }
        }
    }

    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar movimiento")
            .setMessage("¿Estás seguro de que deseas eliminar este movimiento?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    try {
                        val eliminado = ControlGastosProvider.instancia
                            .eliminar(movimientoId)

                        if (eliminado) {
                            Toast.makeText(
                                this@RegistrarMovimientoActivity,
                                "Movimiento eliminado",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        } else {
                            Toast.makeText(
                                this@RegistrarMovimientoActivity,
                                "No se pudo eliminar el movimiento",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RegistrarMovimientoActivity,
                            "Error al eliminar: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .show()
    }
}

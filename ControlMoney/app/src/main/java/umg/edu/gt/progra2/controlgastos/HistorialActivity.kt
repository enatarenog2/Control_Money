package umg.edu.gt.progra2.controlgastos

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.progra2.controlgastos.databinding.ActivityHistorialBinding
import umg.edu.gt.progra2.controlgastos.modelo.Gasto
import umg.edu.gt.progra2.controlgastos.modelo.TipoMovimiento
import java.util.Locale
import android.graphics.Color
import android.graphics.Typeface

class HistorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistorialBinding
    private var movimientos: List<Gasto> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarFiltro()

        binding.btnVolverInicioHistorial.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarMovimientos()
    }

    private fun configurarFiltro() {
        val opciones = listOf(
            getString(R.string.filter_all),
            getString(R.string.filter_income),
            getString(R.string.filter_expenses)
        )

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opciones
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spFiltroMovimientos.adapter = adaptador

        binding.spFiltroMovimientos.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mostrarMovimientosFiltrados()
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                    // No se necesita realizar ninguna acción.
                }
            }
    }

    private fun cargarMovimientos() {
        lifecycleScope.launch {
            try {
                val control = ControlGastosProvider.instancia

                movimientos = control.obtenerTodos()

                val ingresos = control.calcularTotalIngresos()
                val gastos = control.calcularTotalGastos()

                binding.tvHistorialIngresos.text =
                    formatoQuetzales(ingresos)

                binding.tvHistorialGastos.text =
                    formatoQuetzales(gastos)

                mostrarMovimientosFiltrados()

            } catch (_: Exception) {
                Toast.makeText(
                    this@HistorialActivity,
                    R.string.error_loading_movements,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarMovimientosFiltrados() {
        val filtro = binding.spFiltroMovimientos.selectedItem?.toString()
            ?: getString(R.string.filter_all)

        val filtrados = when (filtro) {
            getString(R.string.filter_income) -> movimientos.filter {
                it.tipo == TipoMovimiento.INGRESO
            }

            getString(R.string.filter_expenses) -> movimientos.filter {
                it.tipo == TipoMovimiento.GASTO
            }

            else -> movimientos
        }

        binding.contenedorMovimientos.removeAllViews()

        if (filtrados.isEmpty()) {
            val mensaje = TextView(this).apply {
                setText(R.string.no_movements)
                textSize = 15f
                setTextColor(Color.DKGRAY)
                setPadding(8, 24, 8, 24)
            }

            binding.contenedorMovimientos.addView(mensaje)
            return
        }

        filtrados.sortedByDescending { it.fecha }.forEach { gasto ->
            binding.contenedorMovimientos.addView(
                crearTarjetaMovimiento(gasto)
            )
        }
    }

    private fun crearTarjetaMovimiento(gasto: Gasto): LinearLayout {
        val esIngreso = gasto.tipo == TipoMovimiento.INGRESO

        val tarjeta = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 28, 32, 28)
            setBackgroundResource(R.drawable.fondo_tarjeta)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        val titulo = TextView(this).apply {
            text = gasto.descripcion
            textSize = 17f
            setTextColor(Color.rgb(48, 36, 67))
            setTypeface(null, Typeface.BOLD)
        }

        val tipoTexto = getString(
            if (esIngreso) {
                R.string.movement_type_income
            } else {
                R.string.movement_type_expense
            }
        )

        val detalle = TextView(this).apply {
            text = getString(
                R.string.movement_detail,
                tipoTexto,
                gasto.categoria.name,
                gasto.fecha.toString()
            )
            textSize = 14f
            setTextColor(Color.rgb(113, 107, 128))
            setPadding(0, 12, 0, 0)
        }

        val signo = if (esIngreso) "+" else "−"

        val monto = TextView(this).apply {
            text = getString(
                R.string.movement_amount,
                signo,
                formatoQuetzales(gasto.monto)
            )
            textSize = 18f
            setTypeface(null, Typeface.BOLD)

            setTextColor(
                if (esIngreso) {
                    Color.rgb(35, 134, 83)
                } else {
                    Color.rgb(209, 67, 67)
                }
            )

            setPadding(0, 20, 0, 0)
        }

        tarjeta.addView(titulo)
        tarjeta.addView(detalle)
        tarjeta.addView(monto)

        tarjeta.setOnClickListener {
            val intent = android.content.Intent(
                this,
                RegistrarMovimientoActivity::class.java
            )

            intent.putExtra("movimiento_id", gasto.id)
            startActivity(intent)
        }

        return tarjeta
    }

    private fun formatoQuetzales(monto: Double): String {
        return String.format(Locale.US, "Q %,.2f", monto)
    }

}

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
import umg.edu.gt.progra2.controlgastos.modelo.Categoria

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


    private fun formatoQuetzales(monto: Double): String {
        return String.format(Locale.US, "Q %,.2f", monto)
    }


    private fun crearTarjetaMovimiento(gasto: Gasto): LinearLayout {

        val esIngreso = gasto.tipo == TipoMovimiento.INGRESO


        val colorPrincipal = if (esIngreso) {
            Color.rgb(35, 134, 83)       // Verde para ingresos
        } else {
            Color.rgb(209, 67, 67)       // Rojo para gastos
        }

        val colorFondo = if (esIngreso) {
            Color.rgb(232, 245, 236)     // Fondo verde claro
        } else {
            Color.rgb(253, 236, 236)     // Fondo rojo claro
        }

// Seleccionar icono según el movimiento.
        val icono = if (esIngreso) {

            if (
                gasto.descripcion.contains("sueldo", ignoreCase = true) ||
                gasto.descripcion.contains("salario", ignoreCase = true)
            ) {
                "Q"
            } else {
                "💰"
            }

        } else {

            when (gasto.categoria) {
                Categoria.ALIMENTOS -> "🛒"
                Categoria.TRANSPORTE -> "🚗"
                Categoria.SERVICIOS -> "💡"
                Categoria.ENTRETENIMIENTO -> "🎬"
                Categoria.OTROS -> "📦"
            }
        }

        val signo = if (esIngreso) "+" else "−"

        val tarjeta = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(16, 18, 16, 18)
            setBackgroundResource(R.drawable.fondo_tarjeta)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        }

        // Icono del movimiento.
        val contenedorIcono = TextView(this).apply {
                text = icono
                textSize = 27f
                gravity = android.view.Gravity.CENTER
        }

        tarjeta.addView(
            contenedorIcono,
            LinearLayout.LayoutParams(dp(46), dp(46)).apply {
                marginEnd = dp(12)
            }
        )

        // Información del movimiento.
        val informacion = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val titulo = TextView(this).apply {
            text = gasto.descripcion
            textSize = 15f
            setTextColor(Color.rgb(48, 36, 67))
            setTypeface(null, Typeface.BOLD)
            maxLines = 1
            ellipsize = android.text.TextUtils.TruncateAt.END
        }

        val tipoTexto = if (esIngreso) "Ingreso" else "Gasto"

        val categoria = if (esIngreso) {
            "No aplica"
        } else {
            gasto.categoria.name
                .lowercase(Locale("es", "GT"))
                .replaceFirstChar { it.uppercase() }
        }

        val detalle = TextView(this).apply {
            text = "$tipoTexto · $categoria"
            textSize = 12f
            setTextColor(Color.rgb(113, 107, 128))
            setPadding(0, dp(4), 0, 0)
        }

        val fecha = TextView(this).apply {
            text = gasto.fecha.toString()
            textSize = 11f
            setTextColor(Color.rgb(145, 139, 158))
            setPadding(0, dp(3), 0, 0)
        }

        informacion.addView(titulo)
        informacion.addView(detalle)
        informacion.addView(fecha)

        tarjeta.addView(
            informacion,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        // Monto del movimiento.
        val monto = TextView(this).apply {
            text = "$signo${formatoQuetzales(gasto.monto)}"
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            setTextColor(colorPrincipal)
            gravity = android.view.Gravity.END
            maxLines = 1
        }

        tarjeta.addView(
            monto,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = dp(6)
            }
        )

        // Abrir el movimiento para editarlo.
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

    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }


}

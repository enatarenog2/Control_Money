package umg.edu.gt.progra2.controlgastos

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import umg.edu.gt.progra2.controlgastos.databinding.ActivityReportesBinding
import umg.edu.gt.progra2.controlgastos.modelo.Categoria
import umg.edu.gt.progra2.controlgastos.modelo.Gasto
import umg.edu.gt.progra2.controlgastos.modelo.TipoMovimiento
import java.time.LocalDate
import java.util.Locale

class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding

    private var todosLosMovimientos: List<Gasto> = emptyList()

    private val periodos = listOf(
        "Todo el historial",
        "Mes actual",
        "Año actual"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarSelectorPeriodo()

        binding.btnVolverInicioReportes.setOnClickListener {
            finish()
        }

        cargarMovimientos()
    }

    private fun configurarSelectorPeriodo() {
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            periodos
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spPeriodoReporte.adapter = adaptador

        binding.spPeriodoReporte.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mostrarReporte()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
    }

    private fun cargarMovimientos() {
        lifecycleScope.launch {
            try {
                todosLosMovimientos =
                    ControlGastosProvider.instancia.obtenerTodos()

                mostrarReporte()

            } catch (e: Exception) {
                Toast.makeText(
                    this@ReportesActivity,
                    "No se pudo cargar el reporte.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun mostrarReporte() {
        val hoy = LocalDate.now()

        val movimientosPeriodo = when (
            binding.spPeriodoReporte.selectedItem?.toString()
        ) {
            "Mes actual" -> todosLosMovimientos.filter {
                it.fecha.year == hoy.year &&
                        it.fecha.month == hoy.month
            }

            "Año actual" -> todosLosMovimientos.filter {
                it.fecha.year == hoy.year
            }

            else -> todosLosMovimientos
        }

        val ingresos = movimientosPeriodo
            .filter { it.tipo == TipoMovimiento.INGRESO }
            .sumOf { it.monto }

        val gastos = movimientosPeriodo
            .filter { it.tipo == TipoMovimiento.GASTO }
            .sumOf { it.monto }

        val balance = ingresos - gastos

        binding.tvBalancePeriodo.text = formatoQuetzales(balance)
        binding.tvIngresosPeriodo.text = formatoQuetzales(ingresos)
        binding.tvGastosPeriodo.text = formatoQuetzales(gastos)

        mostrarGastosPorCategoria(movimientosPeriodo)
    }


    private fun mostrarGastosPorCategoria(
        movimientos: List<Gasto>
    ) {
        binding.contenedorCategorias.removeAllViews()

        val gastos = movimientos.filter {
            it.tipo == TipoMovimiento.GASTO
        }

        // Mostrar solamente las categorías con gastos mayores que cero.
        val categoriasConGastos = Categoria.values().filter { categoria ->
            gastos.any {
                it.categoria == categoria && it.monto > 0
            }
        }

        if (categoriasConGastos.isEmpty()) {
            val mensaje = TextView(this).apply {
                text = "Todavía no tienes gastos registrados en este período."
                textSize = 15f
                setTextColor(Color.rgb(113, 107, 128))
                setPadding(0, 12, 0, 12)
            }

            binding.contenedorCategorias.addView(mensaje)
            return
        }

        categoriasConGastos.forEachIndexed { index, categoria ->

            val totalCategoria = gastos
                .filter { it.categoria == categoria }
                .sumOf { it.monto }

            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 8, 0, 16)
            }

            val nombre = TextView(this).apply {
                text = nombreCategoria(categoria)
                textSize = 15f
                setTextColor(Color.rgb(48, 36, 67))
            }

            val monto = TextView(this).apply {
                text = formatoQuetzales(totalCategoria)
                textSize = 17f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.rgb(109, 40, 217))
                setPadding(0, 4, 0, 8)
            }

            fila.addView(nombre)
            fila.addView(monto)

            binding.contenedorCategorias.addView(fila)

            if (index < categoriasConGastos.size - 1) {
                val separador = View(this).apply {
                    setBackgroundColor(Color.rgb(237, 228, 255))
                }

                binding.contenedorCategorias.addView(
                    separador,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                    ).apply {
                        bottomMargin = dp(8)
                    }
                )
            }
        }
    }


    private fun nombreCategoria(categoria: Categoria): String {
        return when (categoria) {
            Categoria.ALIMENTOS -> "Alimentación"
            Categoria.TRANSPORTE -> "Transporte"
            Categoria.SERVICIOS -> "Servicios"
            Categoria.ENTRETENIMIENTO -> "Entretenimiento"
            Categoria.OTROS -> "Otros"
        }
    }

    private fun formatoQuetzales(monto: Double): String {
        return String.format(Locale.US, "Q %,.2f", monto)
    }

    private fun dp(valor: Int): Int {
        return (valor * resources.displayMetrics.density).toInt()
    }
}

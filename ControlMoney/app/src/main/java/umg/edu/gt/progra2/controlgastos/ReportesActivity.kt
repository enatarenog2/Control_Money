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
import android.graphics.drawable.GradientDrawable
import java.time.YearMonth
import java.time.format.DateTimeFormatter

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
        mostrarComparacionSeisMeses()
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

    private fun mostrarComparacionSeisMeses() {

        binding.contenedorComparacion.removeAllViews()

        val mesActual = YearMonth.now()

        // Obtener los últimos seis meses, incluyendo el actual.
        val meses = (5 downTo 0).map { diferencia ->
            mesActual.minusMonths(diferencia.toLong())
        }

        // Calcular ingresos y gastos de cada mes.
        val datosMensuales = meses.map { mes ->

            val movimientosMes = todosLosMovimientos.filter {
                YearMonth.from(it.fecha) == mes
            }

            val ingresos = movimientosMes
                .filter { it.tipo == TipoMovimiento.INGRESO }
                .sumOf { it.monto }

            val gastos = movimientosMes
                .filter { it.tipo == TipoMovimiento.GASTO }
                .sumOf { it.monto }

            Triple(mes, ingresos, gastos)
        }

        // Obtener el valor más alto para escalar las barras.
        val valorMaximo = datosMensuales.maxOfOrNull {
            maxOf(it.second, it.third)
        } ?: 0.0

        val formatoMes = DateTimeFormatter.ofPattern(
            "MMM yyyy",
            Locale("es", "GT")
        )

        datosMensuales.forEachIndexed { index, dato ->

            val (mes, ingresos, gastos) = dato

            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, dp(10), 0, dp(12))
            }

            // Nombre del mes.
            val nombreMes = TextView(this).apply {
                text = mes.format(formatoMes)
                    .replaceFirstChar { it.uppercase() }

                textSize = 15f
                setTextColor(Color.rgb(48, 36, 67))
                setTypeface(null, Typeface.BOLD)
            }

            fila.addView(nombreMes)

            // Barra de ingresos.
            agregarBarraComparacion(
                fila,
                "Ingresos",
                ingresos,
                valorMaximo,
                Color.rgb(35, 134, 83)
            )

            // Barra de gastos.
            agregarBarraComparacion(
                fila,
                "Gastos",
                gastos,
                valorMaximo,
                Color.rgb(109, 40, 217)
            )

            binding.contenedorComparacion.addView(fila)

            // Separador entre meses.
            if (index < datosMensuales.size - 1) {
                val separador = View(this).apply {
                    setBackgroundColor(Color.rgb(237, 228, 255))
                }

                binding.contenedorComparacion.addView(
                    separador,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                    )
                )
            }
        }
    }

    private fun agregarBarraComparacion(
        contenedor: LinearLayout,
        etiqueta: String,
        monto: Double,
        valorMaximo: Double,
        color: Int
    ) {

        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(0, dp(6), 0, dp(4))
        }

        // Nombre del tipo de movimiento.
        val nombre = TextView(this).apply {
            text = etiqueta
            textSize = 12f
            setTextColor(Color.rgb(113, 107, 128))
        }

        fila.addView(
            nombre,
            LinearLayout.LayoutParams(dp(68), dp(24))
        )

        // Calcular el ancho proporcional de la barra.
        val proporcion = if (valorMaximo > 0) {
            monto / valorMaximo
        } else {
            0.0
        }

        val anchoBarra = if (monto > 0) {
            (dp(110) * proporcion).toInt().coerceAtLeast(dp(4))
        } else {
            0
        }

        val barra = View(this).apply {
            background = GradientDrawable().apply {
                setColor(color)
                cornerRadius = dp(8).toFloat()
            }
        }

        fila.addView(
            barra,
            LinearLayout.LayoutParams(anchoBarra, dp(9))
        )

        // Mostrar el monto al lado de la barra.
        val textoMonto = TextView(this).apply {
            text = formatoQuetzales(monto)
            textSize = 11f
            setTextColor(Color.rgb(48, 36, 67))
            setPadding(dp(6), 0, 0, 0)
        }

        fila.addView(
            textoMonto,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        contenedor.addView(fila)
    }
}

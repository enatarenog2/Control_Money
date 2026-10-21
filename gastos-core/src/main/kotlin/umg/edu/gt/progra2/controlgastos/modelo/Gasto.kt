package umg.edu.gt.progra2.controlgastos.modelo

import java.time.LocalDate

data class Gasto(
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val categoria: Categoria,
    val fecha: LocalDate = LocalDate.now(),
    val tipo: TipoMovimiento = TipoMovimiento.GASTO
)

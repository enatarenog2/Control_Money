package umg.edu.gt.progra2.controlgastos.servicio

import umg.edu.gt.progra2.controlgastos.datos.GastoRepository
import umg.edu.gt.progra2.controlgastos.datos.GastoRepositoryEnMemoria
import umg.edu.gt.progra2.controlgastos.modelo.Categoria
import umg.edu.gt.progra2.controlgastos.modelo.Gasto
import umg.edu.gt.progra2.controlgastos.modelo.TipoMovimiento
import java.time.LocalDate

class ControlGastos(
    private val repositorio: GastoRepository = GastoRepositoryEnMemoria()
) {

    suspend fun registrarGasto(
        descripcion: String,
        monto: Double,
        categoria: Categoria,
        fecha: LocalDate = LocalDate.now()
    ): Gasto {
        return registrarMovimiento(
            descripcion,
            monto,
            categoria,
            TipoMovimiento.GASTO,
            fecha
        )
    }

    suspend fun registrarIngreso(
        descripcion: String,
        monto: Double,
        categoria: Categoria,
        fecha: LocalDate = LocalDate.now()
    ): Gasto {
        return registrarMovimiento(
            descripcion,
            monto,
            categoria,
            TipoMovimiento.INGRESO,
            fecha
        )
    }

    private suspend fun registrarMovimiento(
        descripcion: String,
        monto: Double,
        categoria: Categoria,
        tipo: TipoMovimiento,
        fecha: LocalDate
    ): Gasto {
        require(descripcion.isNotBlank()) {
            "La descripción no puede estar vacía"
        }
        require(monto > 0) {
            "El monto debe ser mayor a cero"
        }

        val movimiento = Gasto(
            descripcion = descripcion.trim(),
            monto = monto,
            categoria = categoria,
            fecha = fecha,
            tipo = tipo
        )

        return repositorio.agregar(movimiento)
    }

    suspend fun obtenerTodos(): List<Gasto> {
        return repositorio.listar()
    }

    suspend fun obtenerIngresos(): List<Gasto> {
        return repositorio.listar()
            .filter { it.tipo == TipoMovimiento.INGRESO }
    }

    suspend fun obtenerGastos(): List<Gasto> {
        return repositorio.listar()
            .filter { it.tipo == TipoMovimiento.GASTO }
    }

    suspend fun buscarPorId(id: Int): Gasto? {
        return repositorio.buscarPorId(id)
    }

    suspend fun actualizar(gasto: Gasto): Boolean {
        require(gasto.monto > 0) {
            "El monto debe ser mayor a cero"
        }
        require(gasto.descripcion.isNotBlank()) {
            "La descripción no puede estar vacía"
        }

        return repositorio.actualizar(
            gasto.copy(descripcion = gasto.descripcion.trim())
        )
    }

    suspend fun eliminar(id: Int): Boolean {
        return repositorio.eliminar(id)
    }

    suspend fun calcularTotal(): Double {
        return calcularTotalGastos()
    }

    suspend fun calcularTotalGastos(): Double {
        return obtenerGastos().sumOf { it.monto }
    }

    suspend fun calcularTotalIngresos(): Double {
        return obtenerIngresos().sumOf { it.monto }
    }

    suspend fun calcularSaldo(): Double {
        return calcularTotalIngresos() - calcularTotalGastos()
    }

    suspend fun calcularTotalPorCategoria(
        categoria: Categoria
    ): Double {
        return obtenerGastos()
            .filter { it.categoria == categoria }
            .sumOf { it.monto }
    }
}

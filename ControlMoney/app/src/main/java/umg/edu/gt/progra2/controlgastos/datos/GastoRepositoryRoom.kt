package umg.edu.gt.progra2.controlgastos.datos

import umg.edu.gt.progra2.controlgastos.modelo.Categoria
import umg.edu.gt.progra2.controlgastos.modelo.Gasto
import umg.edu.gt.progra2.controlgastos.modelo.TipoMovimiento
import java.time.LocalDate

class GastoRepositoryRoom(
    private val dao: GastoDao
) : GastoRepository {

    override suspend fun agregar(gasto: Gasto): Gasto {
        val idNuevo = dao.insertar(
            GastoEntity.desde(gasto.copy(id = 0))
        )
        return gasto.copy(id = idNuevo.toInt())
    }

    override suspend fun listar(): List<Gasto> {
        val filas = dao.listarTodos()
        val gastos = mutableListOf<Gasto>()

        for (fila in filas) {
            gastos.add(fila.aGasto())
        }

        return gastos
    }

    override suspend fun buscarPorId(id: Int): Gasto? {
        return dao.buscarPorId(id)?.aGasto()
    }

    override suspend fun actualizar(gasto: Gasto): Boolean {
        return dao.actualizar(GastoEntity.desde(gasto)) > 0
    }

    override suspend fun eliminar(id: Int): Boolean {
        val gasto = dao.buscarPorId(id) ?: return false
        return dao.eliminar(gasto) > 0
    }
}

private fun GastoEntity.aGasto(): Gasto {
    return Gasto(
        id = id,
        descripcion = descripcion,
        monto = monto,
        categoria = Categoria.valueOf(categoria),
        fecha = LocalDate.parse(fecha),
        tipo = TipoMovimiento.valueOf(tipo)
    )
}

private fun GastoEntity.Companion.desde(gasto: Gasto): GastoEntity {
    return GastoEntity(
        id = gasto.id,
        descripcion = gasto.descripcion,
        monto = gasto.monto,
        categoria = gasto.categoria.name,
        fecha = gasto.fecha.toString(),
        tipo = gasto.tipo.name
    )
}

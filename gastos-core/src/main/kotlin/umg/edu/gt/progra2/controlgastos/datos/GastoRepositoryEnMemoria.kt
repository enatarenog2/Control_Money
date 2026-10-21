package umg.edu.gt.progra2.controlgastos.datos


import umg.edu.gt.progra2.controlgastos.modelo.Gasto

class GastoRepositoryEnMemoria : GastoRepository {

    private val gastos = mutableListOf<Gasto>()
    private var siguienteId = 1

    override suspend fun agregar(gasto: Gasto): Gasto {
        val nuevoGasto = gasto.copy(id = siguienteId)
        siguienteId++
        gastos.add(nuevoGasto)
        return nuevoGasto
    }

    override suspend fun listar(): List<Gasto> {
        return gastos.toList()
    }

    override suspend fun buscarPorId(id: Int): Gasto? {
        return gastos.find { it.id == id }
    }

    override suspend fun actualizar(gasto: Gasto): Boolean {
        val posicion = gastos.indexOfFirst { it.id == gasto.id }

        if (posicion == -1) {
            return false
        }

        gastos[posicion] = gasto
        return true
    }

    override suspend fun eliminar(id: Int): Boolean {
        return gastos.removeAll { it.id == id }
    }
}

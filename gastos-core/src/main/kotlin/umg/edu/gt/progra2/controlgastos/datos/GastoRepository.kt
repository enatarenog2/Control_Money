package umg.edu.gt.progra2.controlgastos.datos

import umg.edu.gt.progra2.controlgastos.modelo.Gasto

interface GastoRepository {

    suspend fun agregar(gasto: Gasto): Gasto

    suspend fun listar(): List<Gasto>

    suspend fun buscarPorId(id: Int): Gasto?

    suspend fun actualizar(gasto: Gasto): Boolean

    suspend fun eliminar(id: Int): Boolean
}

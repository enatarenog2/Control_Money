package umg.edu.gt.progra2.controlgastos

import umg.edu.gt.progra2.controlgastos.datos.GastoRepository
import umg.edu.gt.progra2.controlgastos.datos.GastoRepositoryEnMemoria
import umg.edu.gt.progra2.controlgastos.servicio.ControlGastos

object ControlGastosProvider {

    private var repositorio: GastoRepository = GastoRepositoryEnMemoria()

    lateinit var instancia: ControlGastos
        private set

    fun iniciar(nuevoRepositorio: GastoRepository) {
        repositorio = nuevoRepositorio
        instancia = ControlGastos(repositorio)
    }
}

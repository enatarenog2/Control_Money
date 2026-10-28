package umg.edu.gt.progra2.controlgastos

import android.app.Application
import androidx.room.Room
import umg.edu.gt.progra2.controlgastos.datos.AppDatabase
import umg.edu.gt.progra2.controlgastos.datos.GastoRepositoryRoom

class ControlGastosApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val baseDatos = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "controlmoney_db"
        ).build()

        ControlGastosProvider.iniciar(
            GastoRepositoryRoom(baseDatos.gastoDao())
        )
    }
}

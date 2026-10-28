package umg.edu.gt.progra2.controlgastos.datos

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GastoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gastoDao(): GastoDao

}

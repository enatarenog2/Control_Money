package umg.edu.gt.progra2.controlgastos.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GastoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(gasto: GastoEntity): Long

    @Query("SELECT * FROM gastos ORDER BY id ASC")
    suspend fun listarTodos(): List<GastoEntity>

    @Query("SELECT * FROM gastos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): GastoEntity?

    @Update
    suspend fun actualizar(gasto: GastoEntity): Int

    @Delete
    suspend fun eliminar(gasto: GastoEntity): Int

}


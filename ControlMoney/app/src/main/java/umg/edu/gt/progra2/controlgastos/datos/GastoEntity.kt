package umg.edu.gt.progra2.controlgastos.datos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class GastoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val categoria: String,
    val fecha: String,
    val tipo: String
)
{
    companion object
}
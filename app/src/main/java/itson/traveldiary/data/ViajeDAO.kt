package itson.traveldiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ViajeDao {
    @Insert
    fun insert(viaje: Viaje):Long
    @Query("SELECT * FROM Viaje ORDER BY id DESC")
    fun obtenerTodos(): List<Viaje>

    @Query("DELETE FROM Viaje WHERE id = :id")
    fun eliminarViajePorId(id: Int)

    @Update
    fun actualizarViaje(viaje: Viaje)
}
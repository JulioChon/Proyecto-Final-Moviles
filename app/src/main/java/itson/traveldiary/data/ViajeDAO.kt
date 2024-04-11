package itson.traveldiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ViajeDao {
    @Insert
    fun insert(viaje: Viaje):Long
    @Query("SELECT * FROM Viaje ORDER BY id DESC")
    fun obtenerTodos(): List<Viaje>
}
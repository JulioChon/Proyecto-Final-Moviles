package itson.traveldiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PlanificacionDao {
    @Insert
    fun insert(planificacion: Planificacion):Long

    @Query("SELECT * FROM Planificacion WHERE viajeId = :id")
    fun obtenerPlanificacionesPorId(id: Int): List<Planificacion>

    @Query("DELETE FROM Planificacion WHERE viajeId = :id")
    fun eliminarPlanificacionesPorId(id: Int)

    @Update
    fun actualizarPlanificacion(planificacion: Planificacion)
}
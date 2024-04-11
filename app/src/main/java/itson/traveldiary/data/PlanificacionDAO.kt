package itson.traveldiary.data

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface PlanificacionDao {
    @Insert
    fun insert(planificacion: Planificacion):Long
}
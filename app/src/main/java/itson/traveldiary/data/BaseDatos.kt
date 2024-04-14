package itson.traveldiary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Viaje::class, Planificacion::class], version = 3)
abstract class BaseDatos : RoomDatabase() {
    abstract val viajeDao: ViajeDao
    abstract val planificacionDao: PlanificacionDao

    companion object {
        private var INSTANCE: BaseDatos? = null

        fun getInstance(context: Context): BaseDatos {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BaseDatos::class.java,
                    "viajes_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

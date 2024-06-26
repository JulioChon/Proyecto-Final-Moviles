package itson.traveldiary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImagenesDAO {
    @Insert
    fun insert(imagenes: Imagenes):Long

    @Query("SELECT * FROM Imagenes WHERE viajeId = :id")
    fun obtenerImagenesPorId(id: Int): List<Imagenes>

    @Query("DELETE FROM Imagenes WHERE viajeId = :id")
    fun eliminarImagen(id: Int)

    @Query("DELETE FROM Imagenes WHERE direccion = :direccion")
    fun eliminarImagenPorDireccion(direccion: String)
}
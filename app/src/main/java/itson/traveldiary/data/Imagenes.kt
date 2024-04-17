package itson.traveldiary.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Viaje::class,
            parentColumns = ["id"],
            childColumns = ["viajeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Imagenes(
    val viajeId: Int?,
    val direccion: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

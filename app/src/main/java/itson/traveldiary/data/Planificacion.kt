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

data class Planificacion (

    val evento: String,
    val viajeId: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
package itson.traveldiary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Viaje(
    val title: String,
    val image: Int,
    val detail: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

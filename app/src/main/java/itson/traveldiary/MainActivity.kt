package itson.traveldiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val listaViajes = listOf(
            Viaje("Tamazula", R.drawable.ic_launcher_foreground, "Pueblo de Tamazula"),
            Viaje("Guasave", R.drawable.ic_launcher_foreground, "Pueblo de Guasave"),
            Viaje("Ciudad Obregon", R.drawable.ic_launcher_foreground, "Ciudad de Obregon"),
            Viaje("Culiacan", R.drawable.ic_launcher_foreground, "Capital de Sinaloa")
        )

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_cardsViajes)

        val adapter  = CustomadapterCardViajes(listaViajes)


        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
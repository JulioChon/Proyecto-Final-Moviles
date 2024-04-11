package itson.traveldiary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import itson.traveldiary.data.BaseDatos
import itson.traveldiary.data.Viaje
import itson.traveldiary.data.ViajeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viajeDao: ViajeDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val database = BaseDatos.getInstance(applicationContext)
        viajeDao = database.viajeDao

        val addButton = findViewById<Button>(R.id.boton_inferior)
        addButton.setOnClickListener {
            val intent = Intent(this, CreateAlbumActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

       
        CoroutineScope(Dispatchers.IO).launch {
            val viajesOrdenados = viajeDao.obtenerTodos()


            runOnUiThread {
                actualizarRecyclerView(viajesOrdenados)
            }
        }
    }

    private fun actualizarRecyclerView(viajes: List<Viaje>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView_cardsViajes)
        val adapter = CustomadapterCardViajes(viajes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}



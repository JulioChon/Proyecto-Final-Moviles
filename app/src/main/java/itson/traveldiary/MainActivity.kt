package itson.traveldiary

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import itson.traveldiary.data.BaseDatos
import itson.traveldiary.data.Viaje
import itson.traveldiary.data.ViajeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.Manifest
import android.util.Log
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var viajeDao: ViajeDao
    private val REQUEST_CODE_LOCATION_PERMISSION = 123

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

        checkLocationPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarActualizacionUbicacion()
            } else {
                // Aquí puedes mostrar un mensaje al usuario indicando que la funcionalidad de ubicación no estará disponible
                // debido a la falta de permisos.
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            iniciarActualizacionUbicacion()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION
            )
        }
    }

    private fun iniciarActualizacionUbicacion() {
        onResume()
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
        val adapter = CustomadapterCardViajes(viajes, object : CustomadapterCardViajes.OnItemClickListener {
            override fun onItemClick(viaje: Viaje) {
                 lanzarOtraPantalla(viaje)
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun lanzarOtraPantalla (viaje: Viaje){
        val intent = Intent(this, CreateAlbumActivity::class.java).apply {
            putExtra("titulo", viaje.title)
            putExtra("descripcion", viaje.detail)
            putExtra("ubicacion", viaje.ubicacion)
            putExtra("id", viaje.id.toString())
        }
        startActivity(intent)
    }
}
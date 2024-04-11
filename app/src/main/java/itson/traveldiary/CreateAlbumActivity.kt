package itson.traveldiary
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import itson.traveldiary.data.BaseDatos
import itson.traveldiary.data.Planificacion
import itson.traveldiary.data.PlanificacionDao
import itson.traveldiary.data.Viaje
import itson.traveldiary.data.ViajeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CreateAlbumActivity : AppCompatActivity() {

    private lateinit var planificationContainer: LinearLayout
    private lateinit var nombreAlbum: EditText
    private lateinit var descripcionAlbum: EditText
    private lateinit var planificationList: MutableList<String>
    private lateinit var viajeDao: ViajeDao
    private lateinit var planificacionDao: PlanificacionDao
    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
        const val REQUEST_CODE_GALLERY = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_album)

        val database = BaseDatos.getInstance(applicationContext)
        viajeDao = database.viajeDao
        planificacionDao = database.planificacionDao

        nombreAlbum = findViewById(R.id.nombre_album)
        descripcionAlbum = findViewById(R.id.descripcion_album)
        planificationContainer = findViewById(R.id.contenedor_planificacion)
        val botonGuardar =  findViewById<Button>(R.id.boton_guardar)

        botonGuardar.setOnClickListener{
            guardarViaje()
        }

        val botonVolver = findViewById<ImageButton>(R.id.boton_volver)
        botonVolver.setOnClickListener {
            finish()
        }

        val botonAgregarPlanificacion = findViewById<Button>(R.id.boton_agregar_planificacion)
        botonAgregarPlanificacion.setOnClickListener {
            addEditText()
        }


        val botonAgregarFotos = findViewById<ImageButton>(R.id.boton_agregar_fotos)
        botonAgregarFotos.setOnClickListener {
            checkGalleryPermissionAndOpen()
        }
    }

    private fun guardarViaje() {
        val titulo = nombreAlbum.text.toString()
        val descripcion = descripcionAlbum.text.toString()

        val viaje = Viaje(titulo, R.drawable.ic_launcher_foreground, descripcion)

        CoroutineScope(Dispatchers.IO).launch {
            val viajeId = viajeDao.insert(viaje)

            if (viajeId != -1L) {

                guardarPlanificaciones(viajeId)
            } else {

            }
        }
    }

    private suspend fun guardarPlanificaciones(viajeId: Long) {
        for (i in 0 until planificationContainer.childCount) {
            val editText = planificationContainer.getChildAt(i) as EditText
            val texto = editText.text.toString()
            val planificacion = Planificacion(evento = texto, viajeId = viajeId.toInt())


            val planificacionId = planificacionDao.insert(planificacion)
            if(planificacionId!=-1L){
                finish()
            }
        }
    }
    private fun addEditText() {
        val editText = EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = resources.getDimensionPixelSize(R.dimen.default_margin)
            }
            hint = "Descripción de la actividad"
            background = ContextCompat.getDrawable(context, R.drawable.edittext_background)
            setTextColor(Color.BLACK)
        }
        planificationContainer.addView(editText)
    }

    private fun checkGalleryPermissionAndOpen() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGalleryPermissionAndOpen()
                } else {
                    // msj error o algo asi
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            agregarImagenAlGridView(imageUri)
        }
    }

    private fun agregarImagenAlGridView(imageUri: Uri?) {
        val gridView = findViewById<GridView>(R.id.gridView_album_fotos)

        val adapter = gridView.adapter as? ImageAdapter

        if (adapter == null) {
            val listaImagenes = mutableListOf<Uri?>()
            listaImagenes.add(imageUri)
            val nuevoAdapter = ImageAdapter(this, listaImagenes)
            gridView.adapter = nuevoAdapter
        } else {
            adapter.imageUris.add(imageUri)
            adapter.notifyDataSetChanged()
        }
    }

}







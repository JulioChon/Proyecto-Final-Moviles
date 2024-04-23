package itson.traveldiary
import android.annotation.SuppressLint
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
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import itson.traveldiary.data.BaseDatos
import itson.traveldiary.data.Planificacion
import itson.traveldiary.data.PlanificacionDao
import itson.traveldiary.data.Viaje
import itson.traveldiary.data.ViajeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.Manifest
import android.location.Geocoder
import android.os.Environment
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CreateAlbumActivity : AppCompatActivity() {

    private lateinit var planificationContainer: LinearLayout
    private lateinit var nombreAlbum: EditText
    private lateinit var descripcionAlbum: EditText
    private lateinit var botonEliminar: Button
    private lateinit var viajeDao: ViajeDao
    private lateinit var planificacionDao: PlanificacionDao
    private lateinit var campoUbicacion: TextView
    private lateinit var botonGuardar: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var titulo: String
    private lateinit var descripcion: String
    private lateinit var ubicacion: String
    private lateinit var id: String
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri


    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
        const val REQUEST_CODE_GALLERY = 1002
        const val REQUEST_CODE_CAMERA = 1003
        const val REQUEST_IMAGE_CAPTURE = 1004
        var currentPhotoPath: String = ""
        const val FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider"
        const val PERMISSION_REQUEST_CODE_CAMERA = 1005
    }

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_album)
        titulo = intent.getStringExtra("titulo") ?: ""
        descripcion = intent.getStringExtra("descripcion") ?: ""
        ubicacion = intent.getStringExtra("ubicacion") ?: ""
        id = intent.getStringExtra("id") ?: ""



        val database = BaseDatos.getInstance(applicationContext)
        viajeDao = database.viajeDao
        planificacionDao = database.planificacionDao

        nombreAlbum = findViewById(R.id.nombre_album)
        descripcionAlbum = findViewById(R.id.descripcion_album)
        planificationContainer = findViewById(R.id.contenedor_planificacion)
        campoUbicacion = findViewById(R.id.campo_ubicacion)



        botonGuardar = findViewById<Button>(R.id.boton_guardar)
        botonGuardar.setOnClickListener {
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

        val botonCamara = findViewById<ImageButton>(R.id.boton_camara)
        botonCamara.setOnClickListener {
            checkCameraPermissionAndOpen()
        }

         botonEliminar = findViewById<Button>(R.id.boton_eliminar)
        botonEliminar.setOnClickListener{
            eliminarViaje()
        }

       comprobarAccion()
    }

    private fun comprobarAccion(){
        if(titulo.isNotEmpty()){
            nombreAlbum.setText(titulo)
            descripcionAlbum.setText(descripcion)
            campoUbicacion.text = ubicacion
            cargarPlanificacion(id.toInt())
            botonGuardar.setText("Actualizar")
        }else{

            botonEliminar.visibility =  View.INVISIBLE
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            obtenerUbicacion()
        }

    }
    private fun eliminarViaje(){
        CoroutineScope(Dispatchers.IO).launch {
            viajeDao.eliminarViajePorId(id.toInt())
            planificacionDao.eliminarPlanificacionesPorId(id.toInt())
            finish()
        }
    }

    private fun cargarPlanificacion(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val obtenerPlanificacion = planificacionDao.obtenerPlanificacionesPorId(id)
            runOnUiThread {
                planificationContainer.removeAllViews()
                obtenerPlanificacion.forEach { planificacion ->
                    val editText = EditText(this@CreateAlbumActivity).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            topMargin = resources.getDimensionPixelSize(R.dimen.default_margin)
                        }
                        setId(planificacion.id)
                        setText(planificacion.evento)
                        background = ContextCompat.getDrawable(context, R.drawable.edittext_background)
                        setTextColor(Color.BLACK)
                    }
                    planificationContainer.addView(editText)

                }
            }
        }
    }


    private fun obtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses != null) {
                            if (addresses.isNotEmpty()) {
                                val address = StringBuilder()

                                addresses[0].adminArea?.let { state ->
                                    address.append("$state, ")
                                }

                                addresses[0].locality?.let { city ->
                                    address.append("$city, ")
                                }

                                addresses[0].countryName?.let { country ->
                                    address.append(country)
                                }
                                campoUbicacion.text = "Ubicación: $address"
                            } else {
                                campoUbicacion.text = "Ubicación: Desconocida"
                            }
                        }
                    } ?: run {
                        campoUbicacion.text = "Ubicación: No disponible"
                    }
                }
                .addOnFailureListener { e ->
                    campoUbicacion.text = "Ubicación: Error al obtener la ubicación"
                }
        }
    }

    private fun guardarViaje() {
        val titulo = nombreAlbum.text.toString()
        val descripcion = descripcionAlbum.text.toString()
        val ubicacion = campoUbicacion.text.toString()


       if(!id.isNotEmpty())    {
         val viaje = Viaje(titulo, R.drawable.ic_launcher_foreground,ubicacion ,descripcion)

         CoroutineScope(Dispatchers.IO).launch {
            val viajeId = viajeDao.insert(viaje)

            if (viajeId != -1L) {
            guardarPlanificaciones(viajeId)
            finish()
            } else {

        }
        }
       }else{
           println("Entro a actualizar")
           actualizar()
           finish()
       }

    }

    private fun actualizar() {
        val titulo = nombreAlbum.text.toString()
        val descripcion = descripcionAlbum.text.toString()
        val ubicacion = campoUbicacion.text.toString()

        val viaje = Viaje(
            id = id.toInt(),
            title = titulo,
            image = R.drawable.ic_launcher_foreground,
            ubicacion = ubicacion,
            detail = descripcion
        )
        CoroutineScope(Dispatchers.IO).launch {
            viajeDao.actualizarViaje(viaje)
            guardarPlanificaciones(id.toLong())



        }
    }


    private suspend fun guardarPlanificaciones(viajeId: Long) {
        for (i in 0 until planificationContainer.childCount) {

                val editText = planificationContainer.getChildAt(i) as EditText
                val texto = editText.text.toString()
                val planificacionId = editText.id
                if(planificacionId==0){
                    val planificacion = Planificacion(evento = texto, viajeId = viajeId.toInt())

                    planificacionDao.insert(planificacion)
                }else{
                    val planificacion = Planificacion(id = planificacionId,evento = texto, viajeId = viajeId.toInt())

                    planificacionDao.actualizarPlanificacion(planificacion)
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
            setId(0)
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

    private fun checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE_CAMERA
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
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
                    obtenerUbicacion()
                } else {
                    campoUbicacion.text = "Ubicación: Permiso denegado"
                }
            }
            REQUEST_CODE_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    // Permission Denied
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    if (data != null) {
                        val imageUri = data.data
                        agregarImagenAlGridView(imageUri)
                    }
                }
                REQUEST_CODE_CAMERA -> {
                    agregarImagenAlGridView(photoUri)
                }
            }
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







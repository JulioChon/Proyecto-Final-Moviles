package itson.traveldiary
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat


class CreateAlbumActivity : AppCompatActivity() {

    private lateinit var planificationContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_album)

        planificationContainer = findViewById(R.id.contenedor_planificacion)

        val botonVolver = findViewById<ImageButton>(R.id.boton_volver)
        botonVolver.setOnClickListener {
            finish()
        }

        val botonAgregarPlanificacion = findViewById<Button>(R.id.boton_agregar_planificacion)
        botonAgregarPlanificacion.setOnClickListener {
            addEditText()
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

}

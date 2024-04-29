package itson.traveldiary

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import itson.traveldiary.R

class PhotoAdapter(
    private val context: Context,
    private val imageUris: MutableList<Uri?>,
    private val itemClickListener: ItemClickListener // Agregar listener para los clics
) : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(uri: Uri?)
        fun onItemLongClick(uri: Uri?, position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
        return ViewHolder(view).apply {
            // Añadir manejo de clic largo para eliminar
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val uri = imageUris[position]
                    itemClickListener.onItemLongClick(uri, position)
                }
                true
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = imageUris[position]
        Glide.with(context)
            .load(uri)
            .into(holder.imageView)

        // Añadir un clic normal si se necesita
        holder.imageView.setOnClickListener {
            itemClickListener.onItemClick(uri)
        }
    }

    override fun getItemCount() = imageUris.size

    fun addImage(uri: Uri) {
        imageUris.add(uri)
        notifyItemInserted(imageUris.size - 1)
    }

    // Método para eliminar imagen
    fun removeAt(position: Int) {
        imageUris.removeAt(position)
        notifyItemRemoved(position)
    }
}

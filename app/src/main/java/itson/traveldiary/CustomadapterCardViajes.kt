package itson.traveldiary

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import itson.traveldiary.data.Viaje
import com.bumptech.glide.Glide

class CustomadapterCardViajes(
    private val viajes: List<Viaje>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CustomadapterCardViajes.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.imagen_viaje)
        var itemTitle: TextView = itemView.findViewById(R.id.titulo_viaje)
        var itemDetail: TextView = itemView.findViewById(R.id.detalle_viaje)

        init {

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val viaje = viajes[position]
                    listener.onItemClick(viaje)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_viajes, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viaje = viajes[position]


        if (viaje.image.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(viaje.image)
                .into(holder.itemImage)
        } else {
            holder.itemImage.setImageResource(R.drawable.ic_launcher_foreground)

        }


        holder.itemTitle.text = viaje.title
        holder.itemDetail.text = viaje.detail
    }

    override fun getItemCount(): Int {
        return viajes.size
    }


    interface OnItemClickListener {
        fun onItemClick(viaje: Viaje)
    }
}
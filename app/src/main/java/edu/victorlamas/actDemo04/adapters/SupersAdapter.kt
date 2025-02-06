package edu.victorlamas.actDemo04.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.victorlamas.actDemo04.R
import edu.victorlamas.actDemo04.databinding.ItemSupersBinding
import edu.victorlamas.actDemo04.domain.model.SupersWithEditorial

class SupersAdapter(
    // Constructor con los eventos
    private val onClickSuperHero: (SupersWithEditorial) -> Unit,
    private val onClickFavorite: (SupersWithEditorial) -> Unit,
    private val onClickDelete: (SupersWithEditorial) -> Unit
) : ListAdapter< // Hereda de un ListAdapter
        SupersWithEditorial, // con los datos de los súper héroes y sus editoriales
        SupersAdapter.SupersWithEditorialViewHolder // de la manera definida en esta misma clase por el ViewHolder
        >(SupersDiffCallback()) { // incluyendo un comparador de cambios
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): SupersWithEditorialViewHolder {
                // Devuelve un ViewHolder montado y formateado con los datos correspondientes
                return SupersWithEditorialViewHolder(
                    ItemSupersBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ).root
                )
            }

    // Muestra cada item por su posición de la forma determinada por el ViewHolder
    override fun onBindViewHolder(holder: SupersWithEditorialViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Estructura el ViewHolder con las modificaciones sobre el XML y los eventos pertinentes
    inner class SupersWithEditorialViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemSupersBinding.bind(view)
        fun bind(supersWithEditorial: SupersWithEditorial) {
            binding.tvSuperName.text = supersWithEditorial.superHero.superName
            binding.tvEditorial.text = supersWithEditorial.editorial.name
            binding.ivFav.setImageState(
                intArrayOf(R.attr.state_fav_on),
                supersWithEditorial.superHero.favorite == 1
            )
            binding.ivFav.setOnClickListener {
                onClickFavorite(supersWithEditorial)
            }
            binding.ivDel.setOnClickListener {
                onClickDelete(supersWithEditorial)
            }
            itemView.setOnClickListener {
                onClickSuperHero(supersWithEditorial)
            }
        }
    }
}

// Compara dos items por su ID de súper héroe
class SupersDiffCallback : DiffUtil.ItemCallback<SupersWithEditorial>() {
    override fun areItemsTheSame(
        oldItem: SupersWithEditorial,
        newItem: SupersWithEditorial
    ): Boolean {
        return oldItem.superHero.idSuper == newItem.superHero.idSuper
    }

    override fun areContentsTheSame(
        oldItem: SupersWithEditorial,
        newItem: SupersWithEditorial
    ): Boolean {
        return oldItem == newItem
    }
}
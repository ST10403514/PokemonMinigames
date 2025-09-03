package com.mason.pokemonminigames.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.models.Pokemon

class CollectionAdapter(
    private val collection: List<Pokemon>
) : RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    inner class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.ivCollectionPokemon)
        val name: TextView = itemView.findViewById(R.id.tvCollectionPokemonName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collection_pokemon, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        val pokemon = collection[position]
        holder.img.setImageResource(pokemon.imageResId)
        holder.name.text = pokemon.name
    }

    override fun getItemCount(): Int = collection.size
}

package com.mason.pokemonminigames.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.models.Pokemon
import com.mason.pokemonminigames.models.User

class PokemonAdapter(
    private val pokemons: List<Pokemon>,
    private val user: User,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.ivPokemon)
        val name: TextView = itemView.findViewById(R.id.tvPokemonName)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val btn: Button = itemView.findViewById(R.id.btnBuySelect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon_store, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemons[position]
        holder.img.setImageResource(pokemon.imageResId)
        holder.name.text = pokemon.name
        holder.price.text = "${pokemon.price} Coins"

        if (pokemon.isPurchased) {
            holder.btn.text = "Sold Out"
            holder.btn.isEnabled = false
            holder.img.alpha = 0.5f
        } else {
            holder.btn.text = "Buy"
            holder.btn.isEnabled = user.coins >= pokemon.price
            holder.img.alpha = 1f
        }

        holder.btn.setOnClickListener {
            if (!pokemon.isPurchased && user.coins >= pokemon.price) {
                user.coins -= pokemon.price
                pokemon.isPurchased = true
                user.collection.add(pokemon)
                notifyDataSetChanged()
                onUpdate()
            }
        }
    }

    override fun getItemCount(): Int = pokemons.size
}

package com.mason.pokemonminigames.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mason.pokemonminigames.databinding.ItemLeaderboardBinding
import com.mason.pokemonminigames.models.User

class LeaderboardAdapter(
    private val users: List<User>,
    private val currentUserId: String? = null // optional parameter to highlight current user
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.binding.tvName.text = user.username
        holder.binding.tvScore.text = user.highScore.toString()

        // Highlight the current user
        if (user.uid == currentUserId) {
            holder.binding.root.setBackgroundColor(Color.parseColor("#FFE082")) // light yellow
        } else {
            holder.binding.root.setBackgroundColor(Color.TRANSPARENT)
        }
    }
}

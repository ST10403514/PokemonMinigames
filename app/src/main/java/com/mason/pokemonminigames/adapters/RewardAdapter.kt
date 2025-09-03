package com.mason.pokemonminigames.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mason.pokemonminigames.R
import com.mason.pokemonminigames.models.Reward

class RewardAdapter(
    private val rewards: List<Reward>,
    private val onClaim: (Reward) -> Unit
) : RecyclerView.Adapter<RewardAdapter.RewardViewHolder>() {

    inner class RewardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvRewardTitle)
        val desc: TextView = itemView.findViewById(R.id.tvRewardDesc)
        val btnClaim: Button = itemView.findViewById(R.id.btnClaim)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reward, parent, false)
        return RewardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val reward = rewards[position]
        holder.title.text = reward.title
        holder.desc.text = reward.description

        if (reward.isClaimed) {
            holder.btnClaim.text = "Claimed"
            holder.btnClaim.isEnabled = false
        } else {
            holder.btnClaim.text = "Claim"
            holder.btnClaim.isEnabled = true
        }

        holder.btnClaim.setOnClickListener {
            if (!reward.isClaimed) {
                onClaim(reward)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = rewards.size
}

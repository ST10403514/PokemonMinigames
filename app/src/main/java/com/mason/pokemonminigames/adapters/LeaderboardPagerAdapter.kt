package com.mason.pokemonminigames.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mason.pokemonminigames.ui.SinglePlayerLeaderboardFragment
import com.mason.pokemonminigames.ui.MultiplayerLeaderboardFragment

class LeaderboardPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    // 0 = single, 1 = multiplayer
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SinglePlayerLeaderboardFragment()
            1 -> MultiplayerLeaderboardFragment()
            else -> SinglePlayerLeaderboardFragment()
        }
    }
}

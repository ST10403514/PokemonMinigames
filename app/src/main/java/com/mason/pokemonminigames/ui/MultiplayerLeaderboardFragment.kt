package com.mason.pokemonminigames.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mason.pokemonminigames.adapters.LeaderboardAdapter
import com.mason.pokemonminigames.databinding.FragmentLeaderboardBinding
import com.mason.pokemonminigames.models.User

class MultiplayerLeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val userList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        setupRecycler()
        loadLeaderboard()
        return binding.root
    }

    private fun setupRecycler() {
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLeaderboard.adapter = LeaderboardAdapter(userList)
    }

    private fun loadLeaderboard() {
        // Pull multiplayer leaderboard collection (replace with your multiplayer collection name)
        db.collection("multiplayer_leaderboard")
            .orderBy("highScore", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                userList.clear()
                for (doc in snapshot.documents) {
                    val username = doc.getString("username") ?: "Player"
                    val score = doc.getLong("highScore")?.toInt() ?: 0
                    userList.add(User(username = username, highScore = score))
                }
                binding.rvLeaderboard.adapter?.notifyDataSetChanged()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

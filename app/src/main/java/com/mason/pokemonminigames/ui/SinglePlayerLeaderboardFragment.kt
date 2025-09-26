package com.mason.pokemonminigames.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mason.pokemonminigames.adapters.LeaderboardAdapter
import com.mason.pokemonminigames.databinding.FragmentLeaderboardBinding
import com.mason.pokemonminigames.models.User

class SinglePlayerLeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val userList = mutableListOf<User>()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

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
        binding.rvLeaderboard.adapter = LeaderboardAdapter(userList, currentUserId)
    }

    private fun loadLeaderboard() {
        db.collection("singleplayer_leaderboard")
            .orderBy("highScore", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                userList.clear()
                for (doc in snapshot.documents) {
                    val username = doc.getString("username") ?: "Player"
                    val score = doc.getLong("highScore")?.toInt() ?: 0
                    val uid = doc.id
                    userList.add(User(username = username, highScore = score, uid = uid))
                }
                binding.rvLeaderboard.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener {
                // optionally show a toast
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

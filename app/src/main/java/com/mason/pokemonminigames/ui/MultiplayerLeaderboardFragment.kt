package com.mason.pokemonminigames.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.mason.pokemonminigames.adapters.LeaderboardAdapter
import com.mason.pokemonminigames.databinding.FragmentLeaderboardBinding
import com.mason.pokemonminigames.models.LeaderboardResponse
import com.mason.pokemonminigames.models.User
import com.mason.pokemonminigames.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MultiplayerLeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    private val userList = mutableListOf<User>()
    private lateinit var adapter: LeaderboardAdapter
    private val currentUserId: String? by lazy {
        FirebaseAuth.getInstance().currentUser?.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        setupRecycler()
        loadLeaderboard()
        return binding.root
    }

    private fun setupRecycler() {
        // Pass currentUserId to highlight the player's own row
        adapter = LeaderboardAdapter(userList, currentUserId)
        binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLeaderboard.adapter = adapter
    }

    private fun loadLeaderboard() {
        Log.d("LEADERBOARD", "Fetching multiplayer leaderboard from API...")

        RetrofitClient.leaderboardApi.getLeaderboard(limit = 100)
            .enqueue(object : Callback<LeaderboardResponse> {
                override fun onResponse(
                    call: Call<LeaderboardResponse>,
                    response: Response<LeaderboardResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.ok == true) {
                            // Clear and map data in one step
                            userList.clear()
                            userList.addAll(body.leaderboard.map { entry ->
                                User(
                                    username = entry.username,
                                    highScore = entry.highScore
                                )
                            })

                            adapter.notifyDataSetChanged()
                            Log.d("LEADERBOARD", "Loaded ${userList.size} players")
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed: ${body?.error ?: "Unknown error"}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("LEADERBOARD", "API returned ok=false: ${body?.error}")
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e("LEADERBOARD", "HTTP error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<LeaderboardResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("LEADERBOARD", "Network error", t)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

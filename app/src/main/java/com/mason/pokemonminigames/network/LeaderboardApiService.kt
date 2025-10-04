package com.mason.pokemonminigames.network

import com.mason.pokemonminigames.models.LeaderboardResponse
import com.mason.pokemonminigames.models.UpdateScoreRequest
import com.mason.pokemonminigames.models.UpdateScoreResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface LeaderboardAPIService {

    @GET("leaderboard")
    fun getLeaderboard(@Query("limit") limit: Int = 100): Call<LeaderboardResponse>

    @POST("leaderboard/update")
    fun updateScore(@Body request: UpdateScoreRequest): Call<UpdateScoreResponse>
}

package com.example.br_dgetvshowerkotlinapp.network

import com.example.br_dgetvshowerkotlinapp.data.TVData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface TVWebservice {

    @GET("singlesearch/shows")
    fun getTVShow(
        @Query("q") showName: String?
    ): Single<TVData>

}
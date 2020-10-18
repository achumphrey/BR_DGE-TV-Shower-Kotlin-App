package com.example.br_dgetvshowerkotlinapp.repository

import com.example.br_dgetvshowerkotlinapp.data.TVData
import io.reactivex.Single

interface TVRepository {

    fun getTVShowData(user: String): Single<TVData>
}
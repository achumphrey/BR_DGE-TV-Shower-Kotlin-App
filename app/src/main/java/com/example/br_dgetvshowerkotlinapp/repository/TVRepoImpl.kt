package com.example.br_dgetvshowerkotlinapp.repository

import com.example.br_dgetvshowerkotlinapp.data.TVData
import com.example.br_dgetvshowerkotlinapp.network.TVWebservice
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TVRepoImpl (private val webService: TVWebservice) : TVRepository{
    override fun getTVShowData(user:String): Single<TVData> {
        return webService.getTVShow(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}
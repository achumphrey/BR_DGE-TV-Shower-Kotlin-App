package com.example.br_dgetvshowerkotlinapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.br_dgetvshowerkotlinapp.data.TVData
import com.example.br_dgetvshowerkotlinapp.repository.TVRepository
import io.reactivex.disposables.CompositeDisposable
import java.net.UnknownHostException

class TVShowViewModel(private val repo: TVRepository) : ViewModel() {

    private val disposable: CompositeDisposable = CompositeDisposable()
    private val tvData: MutableLiveData<TVData> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun fetchTVData(user:String){

        disposable.add(
            repo.getTVShowData(user)
                .subscribe({
                if (it == null) {
                    errorMessage.value = "No Data Found!"
                } else {
                    tvData.value = it
                }
            }, {
                it.printStackTrace()
                when (it) {
                    is UnknownHostException -> errorMessage.value = "No Network!"
                    else -> errorMessage.value = it.localizedMessage
                }
            })
        )
    }

    fun tvLiveData() : MutableLiveData<TVData>{
        return tvData
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }
}
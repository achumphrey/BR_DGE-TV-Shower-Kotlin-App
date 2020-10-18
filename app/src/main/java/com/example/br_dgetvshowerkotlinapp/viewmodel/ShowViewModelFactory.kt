package com.example.br_dgetvshowerkotlinapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.br_dgetvshowerkotlinapp.repository.TVRepository

@Suppress("UNCHECKED_CAST")
class ShowViewModelFactory (private val repo: TVRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TVShowViewModel(repo) as T
    }
}
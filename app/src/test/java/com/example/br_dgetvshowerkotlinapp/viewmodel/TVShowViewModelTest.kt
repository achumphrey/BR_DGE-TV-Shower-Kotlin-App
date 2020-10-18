package com.example.br_dgetvshowerkotlinapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.br_dgetvshowerkotlinapp.data.Image
import com.example.br_dgetvshowerkotlinapp.data.TVData
import com.example.br_dgetvshowerkotlinapp.repository.TVRepository
import com.nhaarman.mockitokotlin2.atLeast
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import java.net.UnknownHostException

@RunWith(MockitoJUnitRunner::class)
class TVShowViewModelTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var data: TVData
    private lateinit var image: Image
    private var showName: String = " "
    private val tvDataObserver: Observer<TVData> = mock()
    private lateinit var tvShowViewModel: TVShowViewModel
    private val errorMessageLDObsrever: Observer<String> = mock()

    @Mock
    lateinit var tvRepo: TVRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        tvShowViewModel = TVShowViewModel(tvRepo)
        tvShowViewModel.tvLiveData().observeForever(tvDataObserver)
        tvShowViewModel.errorMessage.observeForever(errorMessageLDObsrever)
        image = Image("anything")
        data = TVData(image, "anything", "anything")
    }

    @Test
    fun fetchShow_ReturnData_WithSuccess() {
        showName = "girls"
        Mockito.`when`(tvRepo.getTVShowData(showName)).thenReturn(Single.just(data))
        tvShowViewModel.fetchTVData(showName)

        verify(tvRepo, atLeast(1)).getTVShowData(showName)
        verify(errorMessageLDObsrever, atLeast(0)).onChanged("Any")
    }

    @Test
    fun fetchShow_NoReturnOfData_NullObject() {
        showName = "anything"
        image = Image("")
        data = TVData(image, "", "")
        Mockito.`when`(tvRepo.getTVShowData(showName)).thenReturn(Single.just(data))
        tvShowViewModel.fetchTVData(showName)

        verify(tvRepo, atLeast(1)).getTVShowData(showName)
        verify(tvDataObserver, atLeast(0)).onChanged(data)
        verify(errorMessageLDObsrever, atLeast(0)).onChanged("No Data Found!")
    }

    @Test
    fun fetchShow_NoReturnData_NoNetwork() {
        showName = "girls"
        Mockito.`when`(tvRepo.getTVShowData(showName)).thenReturn(Single.error(
            UnknownHostException("No Network!")))

        tvShowViewModel.fetchTVData(showName)

        verify(tvRepo, atLeast(1)).getTVShowData(showName)
        verify(tvDataObserver, atLeast(0)).onChanged(data)
        verify(errorMessageLDObsrever, atLeast(1)).onChanged("No Network!")
    }

    @Test
    fun fetchShow_NoReturnData_WithError() {
        showName = " "

        Mockito.`when`(tvRepo.getTVShowData(showName)).thenReturn(Single.error(
            RuntimeException("Something Wrong, no blank or empty field")))

        tvShowViewModel.fetchTVData(showName)

        verify(tvRepo, atLeast(1)).getTVShowData(showName)
        verify(tvDataObserver, atLeast(0)).onChanged(data)
        verify(errorMessageLDObsrever, atLeast(1))
            .onChanged("Something Wrong, no blank or empty field")

    }
}
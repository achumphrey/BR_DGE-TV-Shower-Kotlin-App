package com.example.br_dgetvshowerkotlinapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.br_dgetvshowerkotlinapp.data.TVData
import com.example.br_dgetvshowerkotlinapp.network.TVClient
import com.example.br_dgetvshowerkotlinapp.network.TVWebservice
import com.example.br_dgetvshowerkotlinapp.repository.TVRepoImpl
import com.example.br_dgetvshowerkotlinapp.repository.TVRepository
import com.example.br_dgetvshowerkotlinapp.viewmodel.ShowViewModelFactory
import com.example.br_dgetvshowerkotlinapp.viewmodel.TVShowViewModel
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit

class TVShowerActivity : AppCompatActivity() {

    private lateinit var showViewModel: TVShowViewModel
    private var showName: String = ""
    private lateinit var sharedPref: SharedPreferences
    private val MyPreference = "myPref"
    private lateinit var repo: TVRepository
    private lateinit var webServices: TVWebservice


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edName.requestFocus()

        sharedPref = getSharedPreferences(MyPreference, Context.MODE_PRIVATE)
        webServices = TVClient().retrofitInstance
        repo = TVRepoImpl(webServices)

        showViewModel = ViewModelProvider(
                this,
                ShowViewModelFactory(repo))
                .get(TVShowViewModel::class.java)

        showViewModel.tvLiveData().observe(this, Observer {
            it?.let { write(showName, it) }
            displayData(it)
        })

        showViewModel.errorMessage.observe(this, Observer {
            removeViews()
            tvNetworkError.text = it
            tvNetworkError.visibility = View.VISIBLE
            tvError.visibility = View.GONE

        })

        btSearch.setOnClickListener {
            it.hideKeyboard()
            showName = edName.text.toString()

            if (showName.isNotEmpty() && showName.isNotBlank()){

                if (sharedPref.contains(showName)) {
                    useCachedData(showName)
                }else {
                    showViewModel.fetchTVData(showName)
                }
            }else {
                removeViews()
                tvError.visibility = View.VISIBLE
            }
        }
    }

    //Display data
    private fun displayData(data: TVData?){

        removeErrorMsgs()

        tvDays.text = getString(R.string.num_of_days)

        tvName.text = data?.name

        val date: String? = data?.let { calculateNumOfDays(data.premiered) }
        tvNumber.text = date

        Picasso.get()
            .load(data?.image?.original)
            .resize(400,700)
            .error(R.drawable.ic_launcher_background)
            .into(imagView)

        drawViews()
    }

    //Remove error messages
    private fun removeErrorMsgs(){
        tvError.visibility = View.GONE
        tvNetworkError.visibility = View.GONE
    }

    private fun removeViews(){
        tvName.visibility = View.GONE
        tvNumber.visibility = View.GONE
        tvDays.visibility = View.GONE
        imagView.visibility = View.GONE
    }

    //Draw views
    private fun drawViews(){
        tvName.visibility = View.VISIBLE
        tvNumber.visibility = View.VISIBLE
        tvDays.visibility = View.VISIBLE
        imagView.visibility = View.VISIBLE
    }

    //Hide the keyboard
    private fun View.hideKeyboard() {
        val inputManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    //Calculate number of days
    private fun calculateNumOfDays(date: String?) : String{
        val pattern = "yyyy-MM-dd"
        val sdf: DateFormat = SimpleDateFormat(pattern, Locale.UK)

        val z: ZoneId = ZoneId.of("Europe/London")
        val today: LocalDate = LocalDate.now(z)
        val currentDateValue: Date? = sdf.parse("$today")

        val startDateValue: Date? = date?.let { sdf.parse(date)} ?: currentDateValue

        val diff: Long = currentDateValue!!.time - (startDateValue!!.time)
        val numDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

        return numDays.toString()
    }

    private fun write(showName: String?, data: TVData){
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val sData: String = gson.toJson(data)
        editor.putString(showName, sData)
        editor.apply()
    }

    private fun read(showName: String?): TVData{
        var json: String? = null
        if (sharedPref.contains(showName))
            json = sharedPref.getString(showName, "")

        return Gson().fromJson(json, TVData::class.java)
    }

    //Use cached version of data
    private fun useCachedData(showName: String?){
        val data: TVData = read(showName)

        displayData(data)
    }
}
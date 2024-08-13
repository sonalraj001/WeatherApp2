package com.mvvm.weatherapp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.mvvm.weatherapp.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

//7d7f120521718a4141c125533b027a69

class MainActivity : AppCompatActivity() {

    private val binding:ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Jaipur")
        searchCity()

    }
    private fun searchCity(){
        val searchView=binding.searchview
        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {                             
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query !=null) {
                    fetchWeatherData(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()

            .baseUrl("https://api.openweathermap.org/data/2.5")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(apiInterface::class.java)
            
        val response = retrofit.getWeatherData(cityName, "ca9ea40365d04975d87af8202d9bc8af ", "metric")
        response.enqueue(object : Callback<weatherapp> {
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunset.toLong()
                    val sealevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val mintemp = responseBody.main.temp_min
                    val maxtemp = responseBody.main.temp_max

                    binding.temp.text = "$temperature"
                    binding.weather.text = condition
                    binding.maxtemp.text = "Max Temp: $maxtemp C"
                    binding.mintemp.text = "Min Temp: $mintemp C"
                    binding.humidity.text = "$humidity %"
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunrise.text = "${time(sunset)}"
                    binding.sealevel.text = "$sealevel hpa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityName.text = "$cityName"
                    
                    Log.d("TAG","onResponse: $humidity")

                    changeImagesAccordingToWeatherCondition(condition)
                }
            }

            override fun onFailure(call: Call<weatherapp>, t: Throwable) {

            }
        })

    }

    private fun changeImagesAccordingToWeatherCondition(condition: String) {
        when (condition){
            "clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundColor(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Party Clouds","Clouds","Overcast","Mist","Foggy"-> {
                binding.root.setBackgroundColor(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
             }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundColor(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
              }
            "Light Snow","Moderate Snow","Heavy snow","Blizzard"->{
                binding.root.setBackgroundColor(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
               }
            
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE",Locale.getDefault())
        return sdf.format((Date()))
        }   

}


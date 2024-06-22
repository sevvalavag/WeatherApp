package api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WeatherApiService {
    @GET("VisualCrossingWebServices/rest/services/timeline/{latitude},{longitude}?key=AQ3UZP55BP375CPEJGW93MHR6")
    fun getWeatherTimelineByCoordinates(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Call<WeatherTimelineResponse>
}
//https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/{latitude},{longitude}?key=AQ3UZP55BP375CPEJGW93MHR6
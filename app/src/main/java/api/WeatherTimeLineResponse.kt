package api

import com.google.gson.annotations.SerializedName

data class WeatherTimelineResponse(
    @SerializedName("currentConditions")
    val currentConditions: CurrentConditions?
)

data class CurrentConditions(
    @SerializedName("temp")
    val temp: Double?,
    @SerializedName("conditions")
    val conditions: String?,
    @SerializedName("windspeed")
    val windSpeed: Double?
)

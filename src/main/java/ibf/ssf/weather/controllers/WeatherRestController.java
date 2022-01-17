package ibf.ssf.weather.controllers;

import static ibf.ssf.weather.Constants.BEAN_CACHING_WEATHER_SERVICE;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ibf.ssf.weather.error.CityNotFoundException;
import ibf.ssf.weather.models.Weather;
import ibf.ssf.weather.services.WeatherService;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(
    path = "/weather",
    produces = MediaType.APPLICATION_JSON_VALUE
)
public class WeatherRestController {
    private final Logger logger = Logger.getLogger(WeatherRestController.class.getName());

    @Autowired
    @Qualifier(BEAN_CACHING_WEATHER_SERVICE)
    WeatherService weatherService;

    @GetMapping("{city}")
    public ResponseEntity<String> getCity(@PathVariable String city) {
        try {
            List<Weather> weatherList = weatherService.getWeather(city);
            List<JsonObject> weatherJsonList = weatherList.stream()
                                                    .map(Weather::toJson)
                                                    .toList();
            JsonObject weatherJson = Json.createObjectBuilder()
                .add("current_weather", Json.createArrayBuilder(weatherJsonList))
                .add("timestamp", (new Date()).getTime()).build();
            logger.info(weatherJson.toString());
            return ResponseEntity.ok(weatherJson.toString());
        } catch (CityNotFoundException e) {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(errorJson.toString());
        }
    }
}

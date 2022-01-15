package ibf.ssf.weather.services;

import static ibf.ssf.weather.Constants.ENV_OPENWEATHERMAP_KEY;
import static ibf.ssf.weather.Constants.URL_WEATHER;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ibf.ssf.weather.error.CityNotFoundException;
import ibf.ssf.weather.models.Weather;
import ibf.ssf.weather.repositories.WeatherRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class WeatherService {
    private final Logger logger = Logger.getLogger(WeatherService.class.getName());

    @Autowired
    WeatherRepository weatherRepo;

    @Autowired
    WeatherCacheService weatherCacheService;

    private String appId = "";
    public WeatherService() {
        Optional<String> idOpt = Optional.ofNullable(System.getenv(ENV_OPENWEATHERMAP_KEY));
        appId = idOpt.filter(v -> !v.isBlank()).orElseThrow(IllegalArgumentException::new);
    }

    public List<Weather> getWeather(String city) {
        // support multiple spaces between words
        city = String.join("+", city.split("\\s+"));

        // if the results were previously cached and still not expired return it
        Optional<List<Weather>> weatherListOpt = weatherCacheService.get(city);
        if (weatherListOpt.isPresent()) {
            return weatherListOpt.get();
        }

        String url = UriComponentsBuilder
            .fromUriString(URL_WEATHER)
            .queryParam("q", city)
            .queryParam("appid", appId)
            .queryParam("units", "metric")
            .toUriString();

        RequestEntity<Void> request = RequestEntity.get(url).build();
        RestTemplate template = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = template.exchange(request, String.class);
            logger.info(response.getStatusCode().toString());
            logger.info(response.toString());
        } catch (HttpClientErrorException e) {
            logger.info(e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new CityNotFoundException(e.getMessage(), e.getCause());
            }
            return Collections.emptyList();
        }

        final Optional<String> bodyOpt = Optional.ofNullable(response.getBody());
        if (bodyOpt.isEmpty()) {
            return Collections.emptyList();
        }

        try (InputStream inputStream = new ByteArrayInputStream(bodyOpt.get().getBytes());
             JsonReader reader = Json.createReader(inputStream)) {
            final JsonObject result = reader.readObject();
            final JsonArray readings = result.getJsonArray("weather");
            final String cityName = result.getString("name");
            final float temperature = (float) result.getJsonObject("main").getJsonNumber("temp").doubleValue();
            final float feelsTemperature = (float) result.getJsonObject("main").getJsonNumber("feels_like").doubleValue();
            final float minTemperature = (float) result.getJsonObject("main").getJsonNumber("temp_min").doubleValue();
            final float maxTemperature = (float) result.getJsonObject("main").getJsonNumber("temp_max").doubleValue();
            final int humidity = result.getJsonObject("main").getJsonNumber("humidity").intValue();
            final int timestamp = result.getInt("dt");
            final String countryCode = result.getJsonObject("sys").getString("country");

            List<Weather> weatherList = readings.stream()
                .map(JsonObject.class::cast)
                .map(Weather::create)
                .map(v-> {
                    // these values are not found in the readings array so set them here
                    v.setCityName(cityName);
                    v.setTemperature(temperature);
                    v.setFeelsTemperature(feelsTemperature);
                    v.setMinTemperature(minTemperature);
                    v.setMaxTemperature(maxTemperature);
                    v.setHumidity(humidity);
                    v.setTimestamp(timestamp);
                    v.setCountryCode(countryCode);
                    return v;
                })
                .toList();
            // save this to the cache
            weatherCacheService.save(city, weatherList);
            return weatherList;
        } catch (Exception e) {
            logger.severe(Arrays.toString(e.getStackTrace()));
        }
        // instead of throwing exception if no results, just return empty list
        return Collections.emptyList();
    }


}

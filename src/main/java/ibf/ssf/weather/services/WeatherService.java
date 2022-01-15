package ibf.ssf.weather.services;

import static ibf.ssf.weather.Constants.ENV_OPENWEATHERMAP_KEY;
import static ibf.ssf.weather.Constants.URL_WEATHER;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import ibf.ssf.weather.models.Weather;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class WeatherService {
    private final Logger logger = Logger.getLogger(WeatherService.class.getName());

    private String appId = "";
    public WeatherService() {
        Optional<String> idOpt = Optional.ofNullable(System.getenv(ENV_OPENWEATHERMAP_KEY));
        appId = idOpt.filter(v -> !v.isBlank()).orElseThrow(IllegalArgumentException::new);
    }


    public List<Weather> getWeather(String city) {
        // support multiple spaces between words
        city = String.join("+", city.split("\\s+"));

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
            final JsonObject result = reader.readObject();
            final JsonArray readings = result.getJsonArray("weather");
            final String cityName = result.getString("name");
            final float temperature = (float) result.getJsonObject("main").getJsonNumber("temp").doubleValue();

            return readings.stream()
                .map(JsonObject.class::cast)
                .map(Weather::create)
                .map(v-> {
                    v.setCityName(cityName);
                    v.setTemperature(temperature);
                    return v;
                })
                .toList();
        } catch (Exception e) {
            logger.severe(e.getStackTrace().toString());
        }
        return Collections.emptyList();
    }


}

package ibf.ssf.weather.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ibf.ssf.weather.models.Weather;
import ibf.ssf.weather.repositories.WeatherRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class WeatherCacheService {
    private final Logger logger = Logger.getLogger(WeatherCacheService.class.getName());

    @Autowired
    private WeatherRepository weatherRepo;

    public Optional<List<Weather>> get(String city) {
        // the cache can expire so results can be null
        Optional<String> weatherOpt = weatherRepo.get(city);
        if (weatherOpt.isPresent()) {
            // since the data was saved as a string representation of a JsonArray, need to parse into JsonArray
            JsonArray jsonArr = parseJsonArray(weatherOpt.get());
            // use the create from string to load the complete set of saved fields as create from JsonObject only sets 3 fields
            List<Weather> weatherList = jsonArr.stream()
                                        .map(JsonObject.class::cast)
                                        .map(JsonObject::toString)
                                        .map(Weather::create) // creates optionals
                                        .flatMap(Optional::stream) // unpack the optionals that are present into stream of objects
                                        .toList();
            // turn on the cached flag for each Weather object
            weatherList.stream()
                .forEach(weather -> weather.setCached(true));
            return Optional.of(weatherList);
        }
        return Optional.empty();
    }

    public void save(String city, List<Weather> weatherList) {
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        // turn the list of Weather objects into a JsonArray by adding them to the builder
        weatherList.stream()
            .forEach(weather -> arrBuilder.add(weather.toJson()));
        // build the JsonArrayBuilder to get the JsonArray then save the string representation to redis
        weatherRepo.save(city, arrBuilder.build().toString());
    }

    private JsonArray parseJsonArray(String json) {
        // turn the string into ByteArrayInputStream, then read with JsonReader
        try (InputStream inputStream = new ByteArrayInputStream(json.getBytes());
             JsonReader reader = Json.createReader(inputStream)) {
            return reader.readArray();
        } catch (JsonProcessingException jpe) {
            logger.severe("Unable to parse JSON array: %s".formatted(jpe.getMessage()));
        } catch (IOException ioe) {
            logger.severe("Unable to read input String: %s".formatted(ioe.getMessage()));
        }
        // return empty JsonArray
        return Json.createArrayBuilder().build();
    }
}

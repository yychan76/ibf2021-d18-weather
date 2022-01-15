package ibf.ssf.weather.models;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Weather {
    private static final Logger logger = Logger.getLogger(Weather.class.getName());

    private String cityName;
    private float temperature;
    private float feelsTemperature;
    private float minTemperature;
    private float maxTemperature;
    private int humidity; // humidity in percent
    private String main;
    private String description;
    private String icon;
    private int timestamp;
    private boolean cached;


    public String getCityName() {
        return this.cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getFeelsTemperature() {
        return this.feelsTemperature;
    }

    public void setFeelsTemperature(float feelsTemperature) {
        this.feelsTemperature = feelsTemperature;
    }

    public float getMinTemperature() {
        return this.minTemperature;
    }

    public void setMinTemperature(float minTemperature) {
        this.minTemperature = minTemperature;
    }

    public float getMaxTemperature() {
        return this.maxTemperature;
    }

    public void setMaxTemperature(float maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public int getHumidity() {
        return this.humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getMain() {
        return this.main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCached() {
        return this.cached;
    }

    public boolean getCached() {
        return this.cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    public static Weather create(JsonObject jsonObj) {
        // this method is run from an array of API readings JsonObjects that only has these 3 fields
        // other fields need to be set manually at the call site
        Weather weather = new Weather();
        weather.setMain(jsonObj.getString("main"));
        weather.setDescription(jsonObj.getString("description"));
        weather.setIcon(jsonObj.getString("icon"));
        return weather;
    }

    public static Optional<Weather> create(String jsonString) {
        // this method is used to recreate obj from the json string representation stored in redis
        try (InputStream inputStream = new ByteArrayInputStream(jsonString.getBytes());
            JsonReader reader = Json.createReader(inputStream)) {
                JsonObject jsonObj = reader.readObject();
                Weather weather = create(jsonObj);
                weather.setCityName(jsonObj.getString("cityName"));
                weather.setTemperature((float)jsonObj.getJsonNumber("temperature").doubleValue());
                weather.setFeelsTemperature((float)jsonObj.getJsonNumber("feels_like").doubleValue());
                weather.setMinTemperature((float)jsonObj.getJsonNumber("temp_min").doubleValue());
                weather.setMaxTemperature((float)jsonObj.getJsonNumber("temp_max").doubleValue());
                weather.setHumidity(jsonObj.getInt("humidity"));
                weather.setTimestamp(jsonObj.getInt("timestamp"));
                return Optional.of(weather);
        } catch (JsonProcessingException jpe) {
            logger.severe("Unable to parse JSON string: %s".formatted(jpe.getMessage()));
        } catch (IOException ioe) {
            logger.severe("Unable to open JSON string: %s".formatted(ioe.getMessage()));
        }
        return Optional.empty();
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("cityName", cityName)
            .add("main", main)
            .add("description", description)
            .add("icon", icon)
            .add("temperature", temperature)
            .add("feels_like", feelsTemperature)
            .add("temp_min", minTemperature)
            .add("temp_max", maxTemperature)
            .add("humidity", humidity)
            .add("timestamp", timestamp)
            .build();
    }
}

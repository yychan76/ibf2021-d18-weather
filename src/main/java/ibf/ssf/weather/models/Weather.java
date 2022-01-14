package ibf.ssf.weather.models;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Weather {
    private String cityName;
    private float temperature;
    private String main;
    private String description;
    private String icon;

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

    public static Weather create(JsonObject jsonObj) {
        Weather weatherObj = new Weather();
        weatherObj.setMain(jsonObj.getString("main"));
        weatherObj.setDescription(jsonObj.getString("description"));
        weatherObj.setIcon(jsonObj.getString("icon"));
        return weatherObj;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("cityName", cityName)
            .add("main", main)
            .add("description", description)
            .add("icon", icon)
            .add("temperature", temperature)
            .build();
    }
}

package ibf.ssf.weather.services;

import java.util.List;
import ibf.ssf.weather.models.Weather;

public interface WeatherService {
    public List<Weather> getWeather(String city);
}

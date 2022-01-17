package ibf.ssf.weather.services;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ibf.ssf.weather.models.Weather;

import static ibf.ssf.weather.Constants.*;

@Service(BEAN_CACHING_WEATHER_SERVICE)
public class CachingWeatherServiceImpl implements WeatherService{
    private final Logger logger = Logger.getLogger(CachingWeatherServiceImpl.class.getName());

    @Autowired
    private WeatherServiceImpl delegate;

    @Autowired
    private WeatherCacheService cacheSvc;

    public List<Weather> getWeather(String city) {
        // if the results were previously cached and still not expired return it
        Optional<List<Weather>> weatherListOpt = cacheSvc.get(city);
        if (weatherListOpt.isPresent()) {
            logger.info("Cache hit for %s".formatted(city));
            return weatherListOpt.get();
        } else {
            // make a fresh request to the API from the delegate
            List<Weather> weatherList = delegate.getWeather(city);
            // save this to the cache
            cacheSvc.save(city, weatherList);
            return weatherList;
        }
    }
}

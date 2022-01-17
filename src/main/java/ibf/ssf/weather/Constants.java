package ibf.ssf.weather;

import java.time.ZoneId;

public class Constants {
    public static final String URL_WEATHER = "https://api.openweathermap.org/data/2.5/weather";
    public static final String ENV_OPENWEATHERMAP_KEY = "OPENWEATHER_API_KEY";
    public static final long REDIS_KEY_LIFETIME_MINUTES = 10L;
    public static final ZoneId DEFAULT_TIME_ZONE = ZoneId.systemDefault();
    public static final String BEAN_WEATHER_SERVICE = "weatherServiceBean";
    public static final String BEAN_CACHING_WEATHER_SERVICE = "cachingWeatherServiceBean";
}

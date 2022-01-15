package ibf.ssf.weather.repositories;

import static ibf.ssf.weather.Constants.REDIS_KEY_LIFETIME_MINUTES;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherRepository {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void save(String cityName, String value) {
        redisTemplate.opsForValue().set(normalize(cityName), value, REDIS_KEY_LIFETIME_MINUTES, TimeUnit.MINUTES);
    }

    public Optional<String> get(String cityName) {
        String value = redisTemplate.opsForValue().get(normalize(cityName));
        return Optional.ofNullable(value);
    }

    private String normalize(String text) {
        return text.trim().toLowerCase();
    }
}

package ibf.ssf.weather.config;

import java.time.Duration;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
// @PropertySource("application.properties")
public class RedisConfig {
    private final Logger logger = Logger.getLogger(RedisConfig.class.getName());

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.database:0}")
    private int redisDatabase;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Bean
    public JedisPool redisPoolFactory() {
        // not using this
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxTotal(maxActive);
        Optional<String> redisPasswordOpt = Optional.ofNullable(redisPassword);
        if (redisPasswordOpt.isPresent()) {
            logger.info("Redis Password is set from Environment");
            return new JedisPool(jedisPoolConfig, redisHost, redisPort,
                                timeout, redisPasswordOpt.get(), redisDatabase);
        } else {
            return new JedisPool(jedisPoolConfig, redisHost, redisPort,
                                timeout, null, redisDatabase);
        }
    }


    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
        redisStandaloneConfig.setHostName(redisHost);
        redisStandaloneConfig.setPort(redisPort);
        // RedisPassword is a helper class that manages the case where the redisPassword String can be null
        redisStandaloneConfig.setPassword(RedisPassword.of(redisPassword));
        redisStandaloneConfig.setDatabase(redisDatabase);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxActive);
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfig= JedisClientConfiguration.builder();
        jedisClientConfig.connectTimeout(Duration.ofMillis(timeout));
        jedisClientConfig.usePooling().poolConfig(poolConfig);
        return new JedisConnectionFactory(redisStandaloneConfig, jedisClientConfig.build());
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

}

package ua.bala.kafkaproducer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ua.bala.kafkaproducer.model.entity.Telemetry;

@Configuration
public class RedisConfig {

    public static final String AGENT_KEY_PREFIX = "Agent";

    @Value("${redis-cache.host}")
    private String host;

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisConfiguration defaultRedisConfig) {
        return new LettuceConnectionFactory(defaultRedisConfig);
    }

    @Bean
    public RedisConfiguration defaultRedisConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        var split = host.split(":");
        config.setHostName(split[0]);
        config.setPort(Integer.parseInt(split[1]));
        return config;
    }

    @Bean
    public ReactiveRedisTemplate<String, Telemetry> quoteReactiveRedisOperations(ReactiveRedisConnectionFactory factory,
                                                                                 ObjectMapper objectMapper) {
        var serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Telemetry.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, Telemetry> builder = RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        var context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

}

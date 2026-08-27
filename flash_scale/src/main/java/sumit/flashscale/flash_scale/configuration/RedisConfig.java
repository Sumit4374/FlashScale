package sumit.flashscale.flash_scale.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import sumit.flashscale.flash_scale.model.Product;
 
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Product> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Product> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        JacksonJsonRedisSerializer<Product> serializer = new JacksonJsonRedisSerializer<>(Product.class);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return  template;
    }
}

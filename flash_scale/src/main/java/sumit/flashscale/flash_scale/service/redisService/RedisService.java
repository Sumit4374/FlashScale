package sumit.flashscale.flash_scale.service.redisService;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import sumit.flashscale.flash_scale.model.Product;

@Service
public class RedisService {
    
    private final RedisTemplate<String,Product> productRedisTemplate;
    private final RedisTemplate<String,String> lockRedisTemplate;

    private final String LOCK_PREFIX = "lock:product";

    private static final String RELEASE_LOCK_SCRIPT = """
                                                    if redis.call('get', KEYS[1]) == ARGV[1] then
                                                        return redis.call('del', KEYS[1])
                                                    else
                                                        return 0
                                                    end
                                                    """;

    public RedisService(
        @Qualifier("productRedisTemplate") RedisTemplate<String,Product> redisTemplate,
        @Qualifier("lockRedisTemplate") RedisTemplate<String,String> redisTemplateStringObject){
        this.productRedisTemplate = redisTemplate;
        this.lockRedisTemplate = redisTemplateStringObject;
    }

    public boolean acquireLock(Long id, String lockValue){
        String key = LOCK_PREFIX + id;
        boolean aquired = lockRedisTemplate.opsForValue().setIfAbsent(
            key,
            lockValue,
        Duration.ofSeconds(5));
        return Boolean.TRUE.equals(aquired);
    }

    public boolean releaseLock(long id, String lockValue){
        String key = LOCK_PREFIX + id;
        Long result = lockRedisTemplate.execute(
            new DefaultRedisScript<>(
                RELEASE_LOCK_SCRIPT,
            Long.class
            ),
            List.of(key),
            lockValue
        );
        return Long.valueOf(1).equals(result);
    }

    public void set(Product product,Long ttl){
        String key = "product:"+product.getId();
        productRedisTemplate.opsForValue().set(key, product,Duration.ofSeconds(ttl));
    }

    public Product get(Long id){
        String key = "product:"+id;
        Product product = (Product)productRedisTemplate.opsForValue().get(key);
        if(product != null){
            return product;
        }
        return null;
    }

}

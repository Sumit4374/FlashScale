package sumit.flashscale.flash_scale.service.redisService;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import sumit.flashscale.flash_scale.model.Product;

@Service
public class RedisService {
    
    private final RedisTemplate<String,Product> redisTemplate;

    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong cacheMiss = new AtomicLong();

    RedisService(RedisTemplate<String,Product> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public void set(Product product,Long ttl){
        String key = "product:"+product.getId();
        redisTemplate.opsForValue().set(key, product,Duration.ofSeconds(ttl));
    }

    public Product get(Long id){
        String key = "product:"+id;
        Product product = (Product)redisTemplate.opsForValue().get(key);
        if(product != null){
            cacheHits.incrementAndGet();
            return product;
        }
        cacheMiss.incrementAndGet();
        return null;
    }

    public Long getCacheMiss(){
        return cacheMiss.get();
    }

    public Long getCacheHits(){
        return cacheHits.get();
    }
}

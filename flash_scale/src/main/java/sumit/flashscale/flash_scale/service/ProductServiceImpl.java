package sumit.flashscale.flash_scale.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import sumit.flashscale.flash_scale.model.Product;
import sumit.flashscale.flash_scale.repository.ProductRepository;
import sumit.flashscale.flash_scale.service.redisService.RedisService;

@Service
public class ProductServiceImpl implements ProductService{
    
    private final ProductRepository repo;
    private final RedisService redisService;

    private final AtomicLong databaseRead = new AtomicLong();

    public ProductServiceImpl(ProductRepository repo, RedisService redisService){
        this.repo = repo;
        this.redisService = redisService;
    }

    @Override
    public Product get(Long id) {
        Product product = redisService.get(id);
        if (product != null) {
            return product;
        }

        databaseRead.incrementAndGet();
        product = repo.findById(id).orElseThrow(
            () -> new RuntimeException("Product Not found")
        ); 
        Long ttl = 10l;
        redisService.set(product,ttl);
        return product;
    }

    @Override
    public Long getDatabaseRead() {
        return databaseRead.get();
    }

    

}

package sumit.flashscale.flash_scale.service;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
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


    private Product cacheWait(Long id){
        for(int i=0;i<20;i++){
            Product product = redisService.get(id);
            if(product != null){
                return product;
            }

            try{
                Thread.sleep(10);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        throw new RuntimeException(
            "Unable to load product from cache"
        );
    }

    @Override
    public Product get(Long id) throws NotFoundException {
        Product product = redisService.get(id);
        if (product != null) {
            return product;
        }

        String lockValue = UUID.randomUUID().toString();

        if(!redisService.acquireLock(id, lockValue)){
            return cacheWait(id);
        }
        try {
            product = redisService.get(id);
            databaseRead.incrementAndGet();
            product = repo.findById(id).orElseThrow(
                () -> new RuntimeException("Product Not found")
            ); 
            Long ttl = 10l;
            redisService.set(product,ttl);
            return product;
        } finally {
            redisService.releaseLock(id, lockValue);
        }
    }

    @Override
    public Long getDatabaseRead() {
        return databaseRead.get();
    }

    

}

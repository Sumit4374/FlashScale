package sumit.flashscale.flash_scale.service;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import sumit.flashscale.flash_scale.model.Product;
import sumit.flashscale.flash_scale.repository.ProductRepository;
import sumit.flashscale.flash_scale.service.redisService.RedisService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final RedisService redisService;

    private final AtomicLong cacheMiss = new AtomicLong();
    private final AtomicLong cacheHits = new AtomicLong();
    private final AtomicLong databaseRead = new AtomicLong();
    private final AtomicLong lockAcquired = new AtomicLong();
    private final AtomicLong lockFailed = new AtomicLong();
    private final AtomicLong cacheWaits = new AtomicLong();

    public ProductServiceImpl(
            ProductRepository repo,
            RedisService redisService
    ) {
        this.repo = repo;
        this.redisService = redisService;
    }

    private Product cacheWait(Long id) {
        for (int i = 0; i < 40; i++) {
            Product product = redisService.get(id);
            if (product != null) {
                return product;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(
                        "Interrupted while waiting for cache",
                        e
                );
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
            cacheHits.incrementAndGet();
            return product;
        }
        cacheMiss.incrementAndGet();
        String lockValue = UUID.randomUUID().toString();
        boolean acquired = redisService.acquireLock(
                id,
                lockValue
        );
        if (!acquired) {
            lockFailed.incrementAndGet();
            cacheWaits.incrementAndGet();
            return cacheWait(id);
        }
        lockAcquired.incrementAndGet();
        try {
            product = redisService.get(id);
            if (product != null) {
                return product;
            }
            databaseRead.incrementAndGet();
            product = repo.findById(id)
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Product not found"
                            )
                    );
            redisService.set(product, 10L);
            return product;
        } finally {
            redisService.releaseLock(
                    id,
                    lockValue
            );
        }
    }

    @Override
    public Long getDatabaseRead() {
        return databaseRead.get();
    }

    @Override
    public Long getLockAquired() {
        return lockAcquired.get();
    }

    @Override
    public Long getLockFailed() {
        return lockFailed.get();
    }

    @Override
    public Long getCacheMiss() {
        return cacheMiss.get();
    }

    @Override
    public Long getCacheHits() {
        return cacheHits.get();
    }

    @Override
    public Long getCacheWaits() {
        return cacheWaits.get();
    }
}

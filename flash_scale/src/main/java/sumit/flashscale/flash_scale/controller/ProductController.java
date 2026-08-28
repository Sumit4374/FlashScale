package sumit.flashscale.flash_scale.controller;

import org.springframework.web.bind.annotation.*;

import sumit.flashscale.flash_scale.model.Product;
import sumit.flashscale.flash_scale.service.ProductService;
import sumit.flashscale.flash_scale.service.redisService.RedisService;

import java.util.Map;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;




@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService service;
    private final RedisService redisService;

    public ProductController(ProductService service, RedisService redisService){
        this.service = service;
        this.redisService = redisService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Product> getMethodName(@PathVariable Long id) throws NotFoundException{
        
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/cache/status")
    public ResponseEntity<Map<String,Object>> getCacheStatus(){
        Long hits = redisService.getCacheHits();
        Long miss = redisService.getCacheMiss();
        Long total = miss+hits;
        Long databaseReads = service.getDatabaseRead();
        Double ratio = total == 0 ? 0 : (double) hits / total * 100;
        return ResponseEntity.ok(
            Map.of(
                "Hits", hits,
                "Missed",miss,
                "Total",total,
                "DatabaseReads",databaseReads,
                "Ratio",ratio
            )
        );
    }
    
}

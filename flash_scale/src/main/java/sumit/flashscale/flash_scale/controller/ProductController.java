package sumit.flashscale.flash_scale.controller;

import org.springframework.web.bind.annotation.*;

import sumit.flashscale.flash_scale.model.Product;
import sumit.flashscale.flash_scale.service.ProductService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;




@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Value("${INSTANCE_ID:local}")
    private String instanceID;

    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping("/health/load")
    public ResponseEntity<String> loadTest() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Product> getMethodName(@PathVariable Long id) throws NotFoundException{
        
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping("/cache/status")
    public ResponseEntity<Map<String,Object>> getCacheStatus(){
        Long miss = service.getCacheMiss();
        Long hits = service.getCacheHits();
        Long waits = service.getCacheWaits();
        Long total = miss+hits;
        Long databaseReads = service.getDatabaseRead();
        Double ratio = total == 0 ? 0 : (double) hits / total * 100;
        Long lockAquired = service.getLockAquired();
        Long lockFailed = service.getLockFailed();
        return ResponseEntity.ok(
            Map.of(
                "Instance",instanceID,
                "Hits", hits,
                "Missed",miss,
                "Cache wait",waits,
                "Total",total,
                "DatabaseReads",databaseReads,
                "Ratio",ratio,
                "LockAquired",lockAquired,
                "LockFailed",lockFailed
            )
        );
    }
    
}

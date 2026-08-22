package sumit.flashscale.flash_scale.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sumit.flashscale.flash_scale.model.Product;
import sumit.flashscale.flash_scale.service.ProductService;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService service;

    ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Product> getMethodName(@PathVariable Long id) throws NotFoundException {
        return ResponseEntity.ok(service.get(id));
    }
    
    
}

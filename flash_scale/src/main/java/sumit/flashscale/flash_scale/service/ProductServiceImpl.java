package sumit.flashscale.flash_scale.service;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import sumit.flashscale.flash_scale.model.Product;
import sumit.flashscale.flash_scale.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService{
    
    private final ProductRepository repo;

    ProductServiceImpl(ProductRepository repo){
        this.repo = repo;
    }

    @Override
    public Product get(Long id) throws NotFoundException {
        Product product = repo.getById(id);
        return product;
    }

}

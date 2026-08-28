package sumit.flashscale.flash_scale.service;


import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

import sumit.flashscale.flash_scale.model.Product;

public interface ProductService {
    Product get(Long id) throws NotFoundException;
    Long getDatabaseRead();
}

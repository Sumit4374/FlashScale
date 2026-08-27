package sumit.flashscale.flash_scale.service;


import sumit.flashscale.flash_scale.model.Product;

public interface ProductService {
    Product get(Long id) ;
    Long getDatabaseRead();
}

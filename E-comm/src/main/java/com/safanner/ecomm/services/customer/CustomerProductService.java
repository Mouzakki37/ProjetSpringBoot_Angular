package com.safanner.ecomm.services.customer;

import com.safanner.ecomm.dto.ProductDetailDto;
import com.safanner.ecomm.dto.ProductDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CustomerProductService {
    List<ProductDto> getAllProducts();

    List<ProductDto> getAllProductsByName(String name);

    ProductDetailDto getProductDetailById(Long productId);

}

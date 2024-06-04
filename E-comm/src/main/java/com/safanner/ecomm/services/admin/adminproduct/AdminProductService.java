package com.safanner.ecomm.services.admin.adminproduct;

import com.safanner.ecomm.dto.ProductDto;
import io.jsonwebtoken.io.IOException;

import java.util.List;

public interface AdminProductService {

    ProductDto addProduct(ProductDto productDto) throws Exception;

    List<ProductDto> getAllProducts();

    List<ProductDto> getAllProductsByName(String name);

    boolean deleteProduct(Long id);

    ProductDto getProductById(Long productId);

    ProductDto updateProduct(Long productId, ProductDto productDto) throws IOException;
}


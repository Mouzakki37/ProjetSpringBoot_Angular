package com.safanner.ecomm.services.admin.adminproduct;

import com.safanner.ecomm.dto.ProductDto;
import com.safanner.ecomm.entities.Category;
import com.safanner.ecomm.entities.Product;
import com.safanner.ecomm.repositories.CategoryRepository;
import com.safanner.ecomm.repositories.ProductRepository;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;


    public ProductDto addProduct(ProductDto productDto) throws Exception {
        Category category = categoryRepository.findById(productDto.getCategoryId()).orElseThrow();

        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .img(productDto.getImg().getBytes())
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);

        return savedProduct.getDto();
    }

    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    public List<ProductDto> getAllProductsByName(String name) {
        List<Product> products = productRepository.findAllByNameContaining(name);
        return products.stream().map(Product::getDto).collect(Collectors.toList());
    }

    public boolean deleteProduct(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public ProductDto getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.map(Product::getDto).orElse(null);
    }

    public ProductDto updateProduct(Long productId, ProductDto productDto) throws IOException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Optional<Category> optionalCategory = categoryRepository.findById(productDto.getCategoryId());

        if (optionalProduct.isPresent() && optionalCategory.isPresent()) {
            Product product = optionalProduct.get().toBuilder()
                    .name(productDto.getName())
                    .price(productDto.getPrice())
                    .description(productDto.getDescription())
                    .category(optionalCategory.get())
                    .img(productDto.getByteImg() != null ? productDto.getByteImg() : null)
                    .build();
            return productRepository.save(product).getDto();
        }

        return null;
    }
}


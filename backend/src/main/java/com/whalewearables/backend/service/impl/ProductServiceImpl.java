package com.whalewearables.backend.service.impl;

import com.whalewearables.backend.dto.ProductDto;
import com.whalewearables.backend.model.Product;
import com.whalewearables.backend.repository.ProductRepository;
import com.whalewearables.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Override
    public ProductDto saveProducts(ProductDto productDto) {

        Product product = new Product();

        product.setProductId(productDto.getProductId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setCreatedAt(productDto.getCreatedAt());
        product.setUpdatedAt(productDto.getUpdatedAt());

        Product saved = productRepository.save(product);

        ProductDto response = new ProductDto();
        response.setProductId(saved.getProductId());
        response.setName(saved.getName());
        response.setDescription(saved.getDescription());
        response.setPrice(saved.getPrice());
        response.setImageUrl(saved.getImageUrl());
        response.setStockQuantity(saved.getStockQuantity());
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());

        return response;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    ProductDto dto = new ProductDto();
                    dto.setProductId(product.getProductId());
                    dto.setName(product.getName());
                    dto.setDescription(product.getDescription());
                    dto.setPrice(product.getPrice());
                    dto.setImageUrl(product.getImageUrl());
                    dto.setStockQuantity(product.getStockQuantity());
                    dto.setCreatedAt(product.getCreatedAt());
                    dto.setUpdatedAt(product.getUpdatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(Long id) {
        Optional<Product> optProduct = productRepository.findById(id);

        if(optProduct.isEmpty()){
            throw new RuntimeException("Product not found with id"+ id);
        }
        Product product = optProduct.get();
        ProductDto dto = new ProductDto();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    @Override
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id" +id));

        if (productDto.getName() != null)
            existing.setName(productDto.getName());
        if (productDto.getDescription() != null)
            existing.setDescription(productDto.getDescription());
        if (productDto.getPrice() != null)
            existing.setPrice(productDto.getPrice());
        if (productDto.getStockQuantity() != null)
            existing.setStockQuantity(productDto.getStockQuantity());
        if (productDto.getImageUrl() != null)
            existing.setImageUrl(productDto.getImageUrl());

        existing.setUpdatedAt(LocalDateTime.now());

        Product saved = productRepository.save(existing);

        ProductDto response = new ProductDto();
        response.setProductId(saved.getProductId());
        response.setName(saved.getName());
        response.setDescription(saved.getDescription());
        response.setPrice(saved.getPrice());
        response.setStockQuantity(saved.getStockQuantity());
        response.setImageUrl(saved.getImageUrl());
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());

        return response;
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}

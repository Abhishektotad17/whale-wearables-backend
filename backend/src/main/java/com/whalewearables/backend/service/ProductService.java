package com.whalewearables.backend.service;

import com.whalewearables.backend.dto.ProductDto;

import java.util.List;

public interface ProductService {

    public ProductDto saveProducts(ProductDto productDto);
    public List<ProductDto>  getAllProducts();
    public ProductDto getProductById(Long id);
    public ProductDto updateProduct(Long id, ProductDto productDto);
    public void deleteProduct(Long id);
}

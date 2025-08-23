package com.whalewearables.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whalewearables.backend.dto.ProductDto;
import com.whalewearables.backend.service.ProductService;
import com.whalewearables.backend.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private S3Service s3Service;

    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto productDto){
        return productService.saveProducts(productDto);
    }

    @GetMapping
    public List<ProductDto> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id){
        return productService.getProductById(id);
    }
    @PutMapping("/{id}")
    public ProductDto updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto){
        return productService.updateProduct(id, productDto);
    }
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return "Prodcut with id" + id + "deleted successfully";
    }
//    @PostMapping("/upload")
//    public ResponseEntity<String> upload(@RequestParam("file")MultipartFile file) throws IOException {
//        s3Service.uploadFile(file);
//        return ResponseEntity.ok("File uploaded succesfully");
//    }
    @PostMapping(value = "/add-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDto> addProductWithImage(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") BigDecimal price,
            @RequestParam("stockQuantity") int stockQuantity,
            @RequestParam("file") MultipartFile file) throws IOException {

        // 1. Upload image to S3
        String imageUrl = s3Service.uploadFile(file);

        // 2. Create ProductDto manually
        ProductDto productDto = new ProductDto();
        productDto.setName(name);
        productDto.setDescription(description);
        productDto.setPrice(price);
        productDto.setStockQuantity(stockQuantity);
        productDto.setImageUrl(imageUrl);
        productDto.setCreatedAt(LocalDateTime.now());
        productDto.setUpdatedAt(LocalDateTime.now());

        // 3. Save in DB
        ProductDto savedProduct = productService.saveProducts(productDto);

        return ResponseEntity.ok(savedProduct);
    }


}

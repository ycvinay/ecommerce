package com.example.jwt.controller;


import com.example.jwt.model.Product;
import com.example.jwt.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("category") String category,
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam("quantity") int quantity
    ) {

        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setCategory(category);
            if (imageFile != null && !imageFile.isEmpty()) {
                product.setImage(imageFile.getBytes());
            }
            product.setStockQuantity(quantity);
            Product savedProduct = productRepository.save(product);
            savedProduct.setImage(null);
            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        for (Product product: products) {
            if (product.getImage() != null) {
                String base64 = Base64.getEncoder().encodeToString(product.getImage());
                product.setImageBase64(base64);
            }
        }
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> products = productRepository.findById(id);
        if (products.isPresent()) {
            Product product = products.get();
            if (product.getImage() != null) {
                String base64 = Base64.getEncoder().encodeToString(product.getImage());
                product.setImageBase64(base64);
            }
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam String description,
            @RequestParam String category,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam int quantity
    ) {

        try {
            Optional<Product> products = productRepository.findById(id);
            if (products.isPresent()) {
                Product product = products.get();
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setCategory(category);
                if (imageFile != null) {
                    product.setImage(imageFile.getBytes());
                }
                product.setStockQuantity(quantity);
                Product updatedProduct = productRepository.save(product);
                return ResponseEntity.ok(updatedProduct);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<Product> products = productRepository.findAll();
        List<String> categories = products.stream()
                .map(Product::getCategory)
                .distinct()
                .toList();
        return ResponseEntity.ok(categories);
    }


}

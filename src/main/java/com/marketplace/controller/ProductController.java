package com.marketplace.controller;

import com.marketplace.dto.ProductCreateRequest;
import com.marketplace.dto.ApiResponse;
import com.marketplace.service.ProductService;
import com.marketplace.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.marketplace.enums.ProductCategory;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(@ModelAttribute ProductCreateRequest request,
                                                    @RequestParam(value = "documents", required = false) List<MultipartFile> documents,
                                                    @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            // If documents are passed as separate parameter, use them
            if (documents != null && !documents.isEmpty()) {
                request.setDocuments(documents);
            }
            
            Map<String, Object> response = productService.createProduct(request, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestParam(required = false) String category,
                                                  @RequestParam(required = false) String search,
                                                  @RequestParam(required = false) String location) {
        Map<String, Object> response = productService.getProducts(category, search, location);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable String productId) {
        try {
            Map<String, Object> response = productService.getProductDetails(productId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my-products")
    public ResponseEntity<?> getMyProducts(@AuthenticationPrincipal UserPrincipal currentUser) {
        Map<String, Object> response = productService.getMyProducts(currentUser.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        List<Map<String, String>> categories = Arrays.stream(ProductCategory.values())
                .map(category -> {
                    Map<String, String> categoryMap = new HashMap<>();
                    categoryMap.put("value", category.name());
                    categoryMap.put("label", category.getDisplayName());
                    return categoryMap;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        
        return ResponseEntity.ok(response);
    }
}
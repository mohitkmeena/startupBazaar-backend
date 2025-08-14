package com.marketplace.controller;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.ProductCreateRequest;
import com.marketplace.enums.ProductCategory;
import com.marketplace.security.UserPrincipal;
import com.marketplace.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductCreateRequest request,
                                                    @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            ApiResponse response = productService.createProduct(request, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(ApiResponse.success(e.getMessage()));
        }
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse> getProducts(@RequestParam(required = false) String category,
                                                  @RequestParam(required = false) String search,
                                                  @RequestParam(required = false) String location) {
        ApiResponse response = productService.getProducts(category, search, location);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse> getProductDetails(@PathVariable String productId) {
        try {
            ApiResponse response = productService.getProductDetails(productId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.success(e.getMessage()));
        }
    }

    @GetMapping("/my-products")
    public ResponseEntity<ApiResponse> getMyProducts(@AuthenticationPrincipal UserPrincipal currentUser) {
        ApiResponse response = productService.getMyProducts(currentUser.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse> getCategories() {
        List<String> categories = Arrays.stream(ProductCategory.values())
                .map(ProductCategory::getDisplayName)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("categories", categories);
        
        return ResponseEntity.ok(ApiResponse.data(response));
    }
}
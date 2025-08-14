package com.marketplace.service;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.ProductCreateRequest;
import com.marketplace.enums.ProductCategory;
import com.marketplace.enums.UserRole;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public ApiResponse createProduct(ProductCreateRequest request, String userId) {
        User user = userService.findByUserId(userId);
        
        if (user.getRole() != UserRole.SELLER && user.getRole() != UserRole.BOTH) {
            throw new RuntimeException("Only sellers can create products");
        }

        String productId = UUID.randomUUID().toString();
        Product product = new Product(
            productId,
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            request.getName(),
            request.getDescription(),
            request.getCategory(),
            request.getRevenue(),
            request.getAskValue(),
            request.getProfit(),
            request.getLocation(),
            request.getImage(),
            request.getDocuments()
        );

        productRepository.save(product);

        Map<String, String> response = new HashMap<>();
        response.put("product_id", productId);

        return ApiResponse.success("Product created successfully", response);
    }

    public ApiResponse getProducts(String category, String search, String location) {
        List<Product> products;

        if (StringUtils.hasText(search)) {
            products = productRepository.findBySearchTermAndIsActiveTrue(search);
        } else if (StringUtils.hasText(location)) {
            products = productRepository.findByLocationAndIsActiveTrue(location);
        } else if (StringUtils.hasText(category) && !"all".equals(category)) {
            try {
                ProductCategory categoryEnum = ProductCategory.valueOf(category.toUpperCase());
                products = productRepository.findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(categoryEnum);
            } catch (IllegalArgumentException e) {
                products = productRepository.findByIsActiveTrueOrderByCreatedAtDesc();
            }
        } else {
            products = productRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        }

        // Remove sensitive seller information
        products.forEach(product -> product.setSellerEmail(null));

        return ApiResponse.data(Map.of("products", products));
    }

    public ApiResponse getProductDetails(String productId) {
        Product product = productRepository.findByProductIdAndIsActiveTrue(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        // Remove sensitive seller information
        product.setSellerEmail(null);

        return ApiResponse.data(Map.of("product", product));
    }

    public ApiResponse getMyProducts(String userId) {
        List<Product> products = productRepository.findBySellerIdOrderByCreatedAtDesc(userId);
        return ApiResponse.data(Map.of("products", products));
    }
}
package com.marketplace.service;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.ProductCreateRequest;
import com.marketplace.enums.UserRole;
import com.marketplace.exception.ApiException;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.ProductRepository;
import com.marketplace.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Map<String, Object> createProduct(ProductCreateRequest request, String userId) {
        User user = userService.findByUserId(userId);

        if (user.getRole() != UserRole.SELLER && user.getRole() != UserRole.BOTH) {
            throw new ApiException("Only sellers can create products");
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

        return Map.of("product", sanitizeProduct(product));
    }

    public Map<String, Object> getProducts(String incomingCategory, String incomingSearch, String incomingLocation) {
        String category = (incomingCategory == null || incomingCategory.isEmpty() || "all".equals(incomingCategory)) ? "" : incomingCategory;
        String search   = (incomingSearch == null) ? "" : incomingSearch;
        String location = (incomingLocation == null) ? "" : incomingLocation;

        System.out.println("DEBUG: Fetching products with filters - category: '" + category + "', search: '" + search + "', location: '" + location + "'");
        
        List<Product> products;
        try {
            // Try the complex filter query first
            products = productRepository.findByFilters(category, search, location);
            System.out.println("DEBUG: Found " + products.size() + " products from complex filter query");
        } catch (Exception e) {
            System.out.println("DEBUG: Complex filter query failed, using fallback: " + e.getMessage());
            // Fallback to simple query if complex query fails
            products = productRepository.findByIsActiveAndValidDataOrderByCreatedAtDesc();
            System.out.println("DEBUG: Fallback query returned " + products.size() + " products");
        }
        
        // Additional safety check - filter out products with null names and sanitize the rest
        products = products.stream()
                .filter(product -> {
                    boolean isValid = product.getName() != null && !product.getName().trim().isEmpty();
                    if (!isValid) {
                        System.out.println("DEBUG: Filtering out product with null/empty name: " + product.getProductId());
                    }
                    return isValid;
                })
                .map(this::sanitizeProduct)
                .toList();
        
        System.out.println("DEBUG: Returning " + products.size() + " valid products after filtering");
        return Map.of("products", products);
    }

    public Map<String, Object> getProductDetails(String productId) {
        Product product = productRepository.findByProductIdAndIsActiveTrue(productId)
                .orElseThrow(() -> new ApiException("Product not found"));
        
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ApiException("Product data is corrupted - name is missing");
        }
        
        return Map.of("product", sanitizeProduct(product));
    }

    public Map<String, Object> getMyProducts(String userId) {
        List<Product> products;
        try {
            products = productRepository.findBySellerIdOrderByCreatedAtDesc(userId);
        } catch (Exception e) {
            System.out.println("DEBUG: getMyProducts query failed: " + e.getMessage());
            products = new ArrayList<>();
        }
        
        // Filter out products with null names and sanitize the rest
        products = products.stream()
                .filter(product -> {
                    boolean isValid = product.getName() != null && !product.getName().trim().isEmpty();
                    if (!isValid) {
                        System.out.println("DEBUG: Filtering out product with null/empty name: " + product.getProductId());
                    }
                    return isValid;
                })
                .map(this::sanitizeProduct)
                .toList();
        return Map.of("products", products);
    }

    private Product sanitizeProduct(Product product) {
        // Ensure required fields are not null
        if (product.getName() == null) {
            product.setName("Unnamed Product");
        }
        if (product.getDescription() == null) {
            product.setDescription("No description available");
        }
        if (product.getLocation() == null) {
            product.setLocation("Location not specified");
        }
        
        // Hide sensitive seller information
        product.setSellerEmail(null);
        // product.setSellerName(null); // Uncomment if name should also be hidden
        
        return product;
    }
}

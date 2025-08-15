package com.marketplace.service;

import com.marketplace.model.Product;
import com.marketplace.model.DocumentInfo;
import com.marketplace.repository.ProductRepository;
import com.marketplace.dto.ProductCreateRequest;
import com.marketplace.dto.ProductDisplayDto;
import com.marketplace.dto.DocumentDisplayDto;
import com.marketplace.service.S3Service;
import com.marketplace.service.UserService;
import com.marketplace.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.marketplace.enums.ProductCategory;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final S3Service s3Service;
    private final UserService userService;

    public ProductService(ProductRepository productRepository, S3Service s3Service, UserService userService) {
        this.productRepository = productRepository;
        this.s3Service = s3Service;
        this.userService = userService;
    }

    public Map<String, Object> createProduct(ProductCreateRequest request, String userId) {
        try {
            String imageS3Key = null;
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                byte[] imageData = request.getImage().getBytes();
                imageS3Key = s3Service.uploadFile(imageData, request.getImage().getOriginalFilename(), request.getImage().getContentType());
            }

            List<DocumentInfo> documents = new ArrayList<>();
            if (request.getDocuments() != null && !request.getDocuments().isEmpty()) {
                for (MultipartFile doc : request.getDocuments()) {
                    if (doc != null && !doc.isEmpty()) {
                        byte[] docData = doc.getBytes();
                        String docS3Key = s3Service.uploadFile(docData, doc.getOriginalFilename(), doc.getContentType());
                        
                        DocumentInfo docInfo = new DocumentInfo();
                        docInfo.setId(java.util.UUID.randomUUID().toString());
                        docInfo.setFileName(doc.getOriginalFilename());
                        docInfo.setS3Key(docS3Key);
                        docInfo.setContentType(doc.getContentType());
                        docInfo.setFileSize(doc.getSize());
                        documents.add(docInfo);
                    }
                }
            }

            String sellerName = userService.findByUserId(userId).getName();
            
            Product product = new Product(
                java.util.UUID.randomUUID().toString(),
                userId,
                sellerName,
                userService.findByUserId(userId).getEmail(),
                request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getRevenue(),
                request.getAskValue(),
                request.getProfit(),
                request.getLocation(),
                request.getWebsite(),
                imageS3Key,
                documents
            );
            
            productRepository.save(product);
            return Map.of("product", sanitizeProduct(product));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create product: " + e.getMessage());
        }
    }

    public Map<String, Object> getProducts(String category, String search, String location) {
        List<Product> products;
        
        try {
            // Use simple query to avoid conversion errors
            products = productRepository.findByIsActiveTrueOrderByCreatedAtDesc();
        } catch (Exception e) {
            // If there's a conversion error, try to get products without documents field
            try {
                products = productRepository.findByIsActiveTrueWithoutDocuments();
            } catch (Exception e2) {
                // If still failing, try to get products with valid data only
                try {
                    products = productRepository.findByIsActiveAndValidDataOrderByCreatedAtDesc();
                } catch (Exception e3) {
                    // If still failing, return empty list
                    products = new ArrayList<>();
                }
            }
        }
        
        // Apply filters in memory if needed
        if (category != null && !category.trim().isEmpty()) {
            try {
                ProductCategory categoryEnum = ProductCategory.valueOf(category.toUpperCase());
                products = products.stream()
                    .filter(product -> product.getCategory() == categoryEnum)
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid category, return empty list
                products = new ArrayList<>();
            }
        }
        
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            products = products.stream()
                .filter(product -> 
                    (product.getName() != null && product.getName().toLowerCase().contains(searchLower)) ||
                    (product.getDescription() != null && product.getDescription().toLowerCase().contains(searchLower))
                )
                .collect(Collectors.toList());
        }
        
        if (location != null && !location.trim().isEmpty()) {
            String locationLower = location.toLowerCase();
            products = products.stream()
                .filter(product -> 
                    product.getLocation() != null && product.getLocation().toLowerCase().contains(locationLower)
                )
                .collect(Collectors.toList());
        }
        
        List<ProductDisplayDto> productDtos = products.stream()
                .filter(product -> product != null)
                .map(this::sanitizeProduct)
                .collect(Collectors.toList());
        
        return Map.of("products", productDtos);
    }

    public Map<String, Object> getProductDetails(String productId) {
        Product product;
        
        try {
            // First try to find by productId
            product = productRepository.findByProductIdAndIsActiveTrue(productId)
                    .orElseGet(() -> {
                        // If not found, try to find by either productId or id
                        return productRepository.findByProductIdOrIdAndIsActiveTrue(productId)
                                .orElse(null);
                    });
            
            if (product == null) {
                throw new ApiException("Product not found");
            }
        } catch (Exception e) {
            throw new ApiException("Product not found or has invalid data");
        }
        
        return Map.of("product", sanitizeProduct(product));
    }

    public Map<String, Object> getMyProducts(String userId) {
        List<Product> products;
        
        try {
            products = productRepository.findBySellerIdOrderByCreatedAtDesc(userId);
        } catch (Exception e) {
            // If there's a conversion error, try to get products without documents
            try {
                // Create a custom query to get products by sellerId without documents field
                products = productRepository.findAll().stream()
                    .filter(p -> p.getSellerId() != null && p.getSellerId().equals(userId) && p.isActive())
                    .collect(Collectors.toList());
            } catch (Exception e2) {
                products = new ArrayList<>();
            }
        }
        
        List<ProductDisplayDto> productDtos = products.stream()
                .filter(product -> product != null)
                .map(this::sanitizeProduct)
                .collect(Collectors.toList());
        
        return Map.of("products", productDtos);
    }

    private ProductDisplayDto sanitizeProduct(Product product) {
        if (product == null) {
            return null;
        }
        

        
        ProductDisplayDto dto = new ProductDisplayDto();
        
        // Use productId if available, otherwise use MongoDB id
        dto.setProductId(product.getProductId() != null ? product.getProductId() : product.getId());
        dto.setSellerId(product.getSellerId());
        dto.setSellerName(product.getSellerName());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setRevenue(product.getRevenue());
        dto.setAskValue(product.getAskValue());
        dto.setProfit(product.getProfit());
        dto.setLocation(product.getLocation());
        dto.setWebsite(product.getWebsite());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setIsActive(product.isActive());
        
        if (product.getImageS3Key() != null) {
            try {
                String imageUrl = s3Service.generatePresignedUrl(product.getImageS3Key());
                dto.setImageUrl(imageUrl);
            } catch (Exception e) {
                dto.setImageUrl(null);
            }
        }
        
        // Handle documents - support both old and new formats
        try {
            if (product.getDocuments() != null && !product.getDocuments().isEmpty()) {
                List<DocumentDisplayDto> docDtos = product.getDocuments().stream()
                    .filter(doc -> doc != null) // Filter out null documents
                    .map(doc -> {
                        try {
                            DocumentDisplayDto docDto = new DocumentDisplayDto();
                            docDto.setId(doc.getId());
                            docDto.setFileName(doc.getFileName());
                            docDto.setContentType(doc.getContentType());
                            docDto.setFileSize(doc.getFileSize());
                            
                            try {
                                String docUrl = s3Service.generatePresignedUrl(doc.getS3Key());
                                docDto.setUrl(docUrl);
                            } catch (Exception e) {
                                docDto.setUrl(null);
                            }
                            
                            return docDto;
                        } catch (Exception e) {
                            // Skip invalid documents
                            return null;
                        }
                    })
                    .filter(docDto -> docDto != null) // Filter out null DTOs
                    .collect(Collectors.toList());
                
                dto.setDocuments(docDtos);
            } else {
                dto.setDocuments(new ArrayList<>());
            }
        } catch (Exception e) {
            // If there's an error with documents (old format), just set empty list
            dto.setDocuments(new ArrayList<>());
        }
        
        return dto;
    }
}

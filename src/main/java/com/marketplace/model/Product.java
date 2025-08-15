package com.marketplace.model;

import com.marketplace.enums.ProductCategory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Document(collection = "products")
public class Product {
    @Id
    private String id;
    
    private String productId;
    private String sellerId;
    private String sellerName;
    private String sellerEmail;
    private String name;
    private String description;
    private ProductCategory category;
    private Double revenue;
    private Double askValue;
    private Double profit;
    private String location;
    private String image;
    private String website;
    private String imageS3Key;
    
    @Field("documents")
    private Object documents; // Use Object to handle both String and List<DocumentInfo>
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    private boolean isActive = true;



    // Constructors
    public Product() {}

    public Product(String productId, String sellerId, String sellerName, String sellerEmail,
                   String name, String description, ProductCategory category, Double revenue,
                   Double askValue, Double profit, String location, String website, String imageS3Key, List<DocumentInfo> documents) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerEmail = sellerEmail;
        this.name = name;
        this.description = description;
        this.category = category;
        this.revenue = revenue;
        this.askValue = askValue;
        this.profit = profit;
        this.location = location;
        this.website = website;
        this.imageS3Key = imageS3Key;
        this.documents = documents;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }

    public Double getRevenue() { return revenue; }
    public void setRevenue(Double revenue) { this.revenue = revenue; }

    public Double getAskValue() { return askValue; }
    public void setAskValue(Double askValue) { this.askValue = askValue; }

    public Double getProfit() { return profit; }
    public void setProfit(Double profit) { this.profit = profit; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getImageS3Key() { return imageS3Key; }
    public void setImageS3Key(String imageS3Key) { this.imageS3Key = imageS3Key; }

    @SuppressWarnings("unchecked")
    public List<DocumentInfo> getDocuments() { 
        if (documents == null) {
            return new ArrayList<>();
        }
        
        if (documents instanceof List) {
            return (List<DocumentInfo>) documents;
        }
        
        // If it's a String, try to parse it
        if (documents instanceof String) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue((String) documents, new TypeReference<List<DocumentInfo>>() {});
            } catch (Exception e) {
                // Failed to parse documents string
                return new ArrayList<>();
            }
        }
        
        return new ArrayList<>();
    }
    
    public void setDocuments(List<DocumentInfo> documents) { 
        this.documents = documents; 
    }
    
    public void setDocuments(Object documents) { 
        this.documents = documents; 
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
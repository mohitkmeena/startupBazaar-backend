package com.marketplace.dto;

import com.marketplace.enums.ProductCategory;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDisplayDto {
    private String productId;
    private String sellerId;
    private String sellerName;
    private String name;
    private String description;
    private ProductCategory category;
    private Double revenue;
    private Double askValue;
    private Double profit;
    private String location;
    private String website;
    private String imageUrl;
    private List<DocumentDisplayDto> documents;
    private LocalDateTime createdAt;
    private boolean isActive;

    public ProductDisplayDto() {}

    // Getters and Setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

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

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<DocumentDisplayDto> getDocuments() { return documents; }
    public void setDocuments(List<DocumentDisplayDto> documents) { this.documents = documents; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setIsActive(boolean active) { isActive = active; }
} 
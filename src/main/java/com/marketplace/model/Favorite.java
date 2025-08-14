package com.marketplace.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "favorites")
public class Favorite {
    @Id
    private String id;
    
    private String userId;
    private String productId;
    
    @CreatedDate
    private LocalDateTime addedAt;

    // Constructors
    public Favorite() {}

    public Favorite(String userId, String productId) {
        this.userId = userId;
        this.productId = productId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
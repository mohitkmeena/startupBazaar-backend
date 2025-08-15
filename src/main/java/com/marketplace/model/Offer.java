package com.marketplace.model;

import com.marketplace.enums.OfferStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "offers")
public class Offer {
    @Id
    private String id;
    
    private String offerId;
    private String productId;
    private String buyerId;
    private String buyerName;
    private String buyerEmail;
    private String sellerId;
    private Double amount;
    private String message;
    private OfferStatus status;
    private Double counterAmount;
    private String counterMessage;
    private String counterResponse; // "accepted", "rejected", or null
    private String counterResponseMessage;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    private List<Map<String, Object>> history;

    // Constructors
    public Offer() {}

    public Offer(String offerId, String productId, String buyerId, String buyerName,
                 String buyerEmail, String sellerId, Double amount, String message) {
        this.offerId = offerId;
        this.productId = productId;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.sellerId = sellerId;
        this.amount = amount;
        this.message = message;
        this.status = OfferStatus.PENDING;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOfferId() { return offerId; }
    public void setOfferId(String offerId) { this.offerId = offerId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public String getBuyerEmail() { return buyerEmail; }
    public void setBuyerEmail(String buyerEmail) { this.buyerEmail = buyerEmail; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OfferStatus getStatus() { return status; }
    public void setStatus(OfferStatus status) { this.status = status; }

    public Double getCounterAmount() { return counterAmount; }
    public void setCounterAmount(Double counterAmount) { this.counterAmount = counterAmount; }

    public String getCounterMessage() { return counterMessage; }
    public void setCounterMessage(String counterMessage) { this.counterMessage = counterMessage; }

    public String getCounterResponse() { return counterResponse; }
    public void setCounterResponse(String counterResponse) { this.counterResponse = counterResponse; }

    public String getCounterResponseMessage() { return counterResponseMessage; }
    public void setCounterResponseMessage(String counterResponseMessage) { this.counterResponseMessage = counterResponseMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<Map<String, Object>> getHistory() { return history; }
    public void setHistory(List<Map<String, Object>> history) { this.history = history; }
}
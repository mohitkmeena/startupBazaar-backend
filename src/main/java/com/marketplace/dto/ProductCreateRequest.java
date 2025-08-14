package com.marketplace.dto;

import com.marketplace.enums.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.util.List;

public class ProductCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Category is required")
    private ProductCategory category;
    
    @NotNull(message = "Revenue is required")
    @DecimalMin(value = "0.0", message = "Revenue must be positive")
    private Double revenue;
    
    @NotNull(message = "Ask value is required")
    @DecimalMin(value = "0.0", message = "Ask value must be positive")
    private Double askValue;
    
    @NotNull(message = "Profit is required")
    @DecimalMin(value = "0.0", message = "Profit must be positive")
    private Double profit;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String image;
    private List<String> documents;

    // Constructors
    public ProductCreateRequest() {}

    // Getters and Setters
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

    public List<String> getDocuments() { return documents; }
    public void setDocuments(List<String> documents) { this.documents = documents; }
}
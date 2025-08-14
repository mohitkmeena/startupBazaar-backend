package com.marketplace.enums;

public enum ProductCategory {
    SAAS("SaaS"),
    FINTECH("Fintech"),
    ECOMMERCE("E-commerce"),
    EDTECH("EdTech"),
    HEALTHTECH("HealthTech"),
    FOODTECH("FoodTech"),
    PROPTECH("PropTech"),
    GAMING("Gaming"),
    SOCIAL_MEDIA("Social Media"),
    OTHER("Other");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
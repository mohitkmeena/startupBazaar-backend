package com.marketplace.dto;

import jakarta.validation.constraints.NotBlank;

public class CounterOfferResponseRequest {
    @NotBlank(message = "Response type is required")
    private String responseType; // "accept" or "reject"
    
    private String message;

    // Constructors
    public CounterOfferResponseRequest() {}

    // Getters and Setters
    public String getResponseType() { return responseType; }
    public void setResponseType(String responseType) { this.responseType = responseType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
} 
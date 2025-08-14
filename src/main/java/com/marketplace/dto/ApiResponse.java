package com.marketplace.dto;

import java.util.Map;

public class ApiResponse {
    private String message;
    private Object data;

    public ApiResponse() {}

    public ApiResponse(String message) {
        this.message = message;
    }

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(message);
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(message, data);
    }

    public static ApiResponse data(Object data) {
        return new ApiResponse(null, data);
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
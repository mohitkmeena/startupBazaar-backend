package com.marketplace.exception;

public class ApiException extends RuntimeException{
    public ApiException(String productNotFound) {
        super(productNotFound);
    }
}

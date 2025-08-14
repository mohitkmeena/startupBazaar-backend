package com.marketplace.controller;

import com.marketplace.dto.ApiResponse;
import com.marketplace.security.UserPrincipal;
import com.marketplace.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse> addToFavorites(@PathVariable String productId,
                                                     @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            ApiResponse response = favoriteService.addToFavorites(productId, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.success(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.success(e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse> removeFromFavorites(@PathVariable String productId,
                                                          @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            ApiResponse response = favoriteService.removeFromFavorites(productId, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.success(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getFavorites(@AuthenticationPrincipal UserPrincipal currentUser) {
        ApiResponse response = favoriteService.getFavorites(currentUser.getUserId());
        return ResponseEntity.ok(response);
    }
}
package com.marketplace.controller;

import com.marketplace.dto.ApiResponse;
import com.marketplace.security.UserPrincipal;
import com.marketplace.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<?> addToFavorites(@PathVariable String productId,
                                                     @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Map<String, Object> response = favoriteService.addToFavorites(productId, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable String productId,
                                                          @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Map<String, Object> response = favoriteService.removeFromFavorites(productId, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getFavorites(@AuthenticationPrincipal UserPrincipal currentUser) {
        Map<String, Object> response = favoriteService.getFavorites(currentUser.getUserId());
        return ResponseEntity.ok(response);
    }
}
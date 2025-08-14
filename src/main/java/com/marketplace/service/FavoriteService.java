package com.marketplace.service;

import com.marketplace.dto.ApiResponse;
import com.marketplace.model.Favorite;
import com.marketplace.model.Product;
import com.marketplace.repository.FavoriteRepository;
import com.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.productRepository = productRepository;
    }

    public ApiResponse addToFavorites(String productId, String userId) {
        // Check if product exists
        Product product = productRepository.findByProductIdAndIsActiveTrue(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if already in favorites
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product already in favorites");
        }

        Favorite favorite = new Favorite(userId, productId);
        favoriteRepository.save(favorite);

        return ApiResponse.success("Product added to favorites");
    }

    public ApiResponse removeFromFavorites(String productId, String userId) {
        if (!favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("Product not in favorites");
        }

        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
        return ApiResponse.success("Product removed from favorites");
    }

    public ApiResponse getFavorites(String userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        List<Product> favoriteProducts = new ArrayList<>();

        for (Favorite favorite : favorites) {
            productRepository.findByProductIdAndIsActiveTrue(favorite.getProductId())
                .ifPresent(product -> {
                    // Remove sensitive seller information
                    product.setSellerEmail(null);
                    favoriteProducts.add(product);
                });
        }

        return ApiResponse.data(Map.of("favorites", favoriteProducts));
    }
}
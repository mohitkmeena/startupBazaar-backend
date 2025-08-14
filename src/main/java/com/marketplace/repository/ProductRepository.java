package com.marketplace.repository;

import com.marketplace.model.Product;
import com.marketplace.enums.ProductCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByProductIdAndIsActiveTrue(String productId);
    List<Product> findBySellerIdOrderByCreatedAtDesc(String sellerId);
    List<Product> findByIsActiveTrueOrderByCreatedAtDesc();
    List<Product> findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(ProductCategory category);
    
    @Query("{ $and: [ { 'isActive': true }, { $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] } ] }")
    List<Product> findBySearchTermAndIsActiveTrue(String searchTerm);
    
    @Query("{ $and: [ { 'isActive': true }, { 'location': { $regex: ?0, $options: 'i' } } ] }")
    List<Product> findByLocationAndIsActiveTrue(String location);
}
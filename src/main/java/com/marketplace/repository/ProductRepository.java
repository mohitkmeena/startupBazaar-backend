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
    
    // Fallback method to find by either productId or id
    @Query("{ $and: [ { 'isActive': true }, { $or: [ { 'productId': ?0 }, { '_id': ?0 } ] } ] }")
    Optional<Product> findByProductIdOrIdAndIsActiveTrue(String productId);
    List<Product> findBySellerIdOrderByCreatedAtDesc(String sellerId);
    List<Product> findByIsActiveTrueOrderByCreatedAtDesc();
    List<Product> findByCategoryAndIsActiveTrueOrderByCreatedAtDesc(ProductCategory category);
    
    @Query("{ $and: [ { 'isActive': true }, { $or: [ { 'name': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] } ] }")
    List<Product> findBySearchTermAndIsActiveTrue(String searchTerm);
    
    @Query("{ $and: [ { 'isActive': true }, { 'location': { $regex: ?0, $options: 'i' } } ] }")
    List<Product> findByLocationAndIsActiveTrue(String location);
    @Query("""
{ 
  $and: [
    { "isActive": true },
    { "name": { $exists: true, $ne: null, $ne: "" } },
    { "description": { $exists: true, $ne: null, $ne: "" } },
    { "location": { $exists: true, $ne: null, $ne: "" } },
    { $or: [
        { ?0: null },
        { ?0: "" },
        { "category": ?0 }
      ]
    },
    { $or: [
        { ?1: null },
        { ?1: "" },
        { "name": { $regex: ?1, $options: "i" } },
        { "description": { $regex: ?1, $options: "i" } }
      ]
    },
    { $or: [
        { ?2: null },
        { ?2: "" },
        { "location": { $regex: ?2, $options: "i" } }
      ]
    }
  ]
}
""")
    List<Product> findByFilters(String category, String search, String location);
    
    // Fallback method to get all active products with valid data
    @Query("{ $and: [ { 'isActive': true }, { 'name': { $exists: true, $ne: null, $ne: '' } }, { 'description': { $exists: true, $ne: null, $ne: '' } }, { 'location': { $exists: true, $ne: null, $ne: '' } } ] }")
    List<Product> findByIsActiveAndValidDataOrderByCreatedAtDesc();
    
    // Method to get products without documents field to avoid conversion issues
    @Query(value = "{ 'isActive': true }", fields = "{ 'documents': 0 }")
    List<Product> findByIsActiveTrueWithoutDocuments();

}
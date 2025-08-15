package com.marketplace.repository;

import com.marketplace.model.Offer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferRepository extends MongoRepository<Offer, String> {
    Optional<Offer> findByOfferId(String offerId);
    List<Offer> findBySellerIdOrderByCreatedAtDesc(String sellerId);
    List<Offer> findByBuyerIdOrderByCreatedAtDesc(String buyerId);
    List<Offer> findByProductIdOrderByCreatedAtDesc(String productId);
}
package com.marketplace.service;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.CounterOfferRequest;
import com.marketplace.dto.CounterOfferResponseRequest;
import com.marketplace.dto.OfferCreateRequest;
import com.marketplace.enums.OfferStatus;
import com.marketplace.enums.UserRole;
import com.marketplace.model.Offer;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.OfferRepository;
import com.marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OfferService {

    private final OfferRepository offerRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public OfferService(OfferRepository offerRepository, ProductRepository productRepository, 
                       UserService userService) {
        this.offerRepository = offerRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Map<String, Object> createOffer(OfferCreateRequest request, String userId) {
        User user = userService.findByUserId(userId);
        
        if (user.getRole() != UserRole.BUYER && user.getRole() != UserRole.BOTH) {
            throw new RuntimeException("Only buyers can make offers");
        }

        Product product = productRepository.findByProductIdAndIsActiveTrue(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getSellerId().equals(userId)) {
            throw new RuntimeException("Cannot make offer on your own product");
        }

        String offerId = UUID.randomUUID().toString();
        Offer offer = new Offer(
            offerId,
            request.getProductId(),
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            product.getSellerId(),
            request.getAmount(),
            request.getMessage()
        );

        offerRepository.save(offer);

        return Map.of("offer_id", offerId);
    }

    public Map<String, Object> acceptOffer(String offerId, String userId) {
        Offer offer = offerRepository.findByOfferId(offerId)
            .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getSellerId().equals(userId)) {
            throw new RuntimeException("Only seller can accept offer");
        }

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new RuntimeException("Offer is not pending");
        }

        // Update offer status and add to history
        offer.setStatus(OfferStatus.ACCEPTED);
        if (offer.getHistory() == null) {
            offer.setHistory(new ArrayList<>());
        }
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("action", "accepted");
        historyEntry.put("timestamp", LocalDateTime.now());
        historyEntry.put("by", userId);
        offer.getHistory().add(historyEntry);

        offerRepository.save(offer);

        // Get both buyer and seller details for contact exchange
        User seller = userService.findByUserId(offer.getSellerId());
        User buyer = userService.findByUserId(offer.getBuyerId());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Offer accepted successfully");
        response.put("buyer_contact", Map.of(
            "name", buyer.getName(),
            "email", buyer.getEmail(),
            "phone", buyer.getPhone()
        ));
        response.put("seller_contact", Map.of(
            "name", seller.getName(),
            "email", seller.getEmail(),
            "phone", seller.getPhone()
        ));

        return response;
    }

    public Map<String, Object> rejectOffer(String offerId, String userId) {
        Offer offer = offerRepository.findByOfferId(offerId)
            .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getSellerId().equals(userId)) {
            throw new RuntimeException("Only seller can reject offer");
        }

        if (offer.getStatus() != OfferStatus.PENDING) {
            throw new RuntimeException("Offer is not pending");
        }

        // Update offer status and add to history
        offer.setStatus(OfferStatus.REJECTED);
        if (offer.getHistory() == null) {
            offer.setHistory(new ArrayList<>());
        }
        Map<String, Object> historyEntry = new HashMap<>();
        historyEntry.put("action", "rejected");
        historyEntry.put("timestamp", LocalDateTime.now());
        historyEntry.put("by", userId);
        offer.getHistory().add(historyEntry);

        offerRepository.save(offer);

        return Map.of("message", "Offer rejected successfully");
    }

    public Map<String, Object> counterOffer(String offerId, CounterOfferRequest request, String userId) {
        Offer offer = offerRepository.findByOfferId(offerId)
            .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (request.getResponseType() != null) {
            // This is a buyer responding to a counter offer
            if (!offer.getBuyerId().equals(userId)) {
                throw new RuntimeException("Only buyer can respond to counter offer");
            }
            
            if (offer.getStatus() != OfferStatus.COUNTERED) {
                throw new RuntimeException("No counter offer to respond to");
            }
            
            if ("accept".equals(request.getResponseType())) {
                // Buyer accepts counter offer
                offer.setStatus(OfferStatus.COUNTER_ACCEPTED);
                offer.setCounterResponse("accepted");
                offer.setCounterResponseMessage(request.getMessage());
                
                // Add to history
                if (offer.getHistory() == null) {
                    offer.setHistory(new ArrayList<>());
                }
                Map<String, Object> historyEntry = new HashMap<>();
                historyEntry.put("action", "counter_accepted");
                historyEntry.put("message", request.getMessage());
                historyEntry.put("timestamp", LocalDateTime.now());
                historyEntry.put("by", userId);
                offer.getHistory().add(historyEntry);
                
                offerRepository.save(offer);
                
                // Get both buyer and seller details for contact exchange
                User seller = userService.findByUserId(offer.getSellerId());
                User buyer = userService.findByUserId(offer.getBuyerId());
                
                return Map.of(
                    "message", "Counter offer accepted successfully",
                    "buyer_contact", Map.of(
                        "name", buyer.getName(),
                        "email", buyer.getEmail(),
                        "phone", buyer.getPhone()
                    ),
                    "seller_contact", Map.of(
                        "name", seller.getName(),
                        "email", seller.getEmail(),
                        "phone", seller.getPhone()
                    )
                );
                
            } else if ("reject".equals(request.getResponseType())) {
                // Buyer rejects counter offer
                offer.setStatus(OfferStatus.COUNTER_REJECTED);
                offer.setCounterResponse("rejected");
                offer.setCounterResponseMessage(request.getMessage());
                
                // Add to history
                if (offer.getHistory() == null) {
                    offer.setHistory(new ArrayList<>());
                }
                Map<String, Object> historyEntry = new HashMap<>();
                historyEntry.put("action", "counter_rejected");
                historyEntry.put("message", request.getMessage());
                historyEntry.put("timestamp", LocalDateTime.now());
                historyEntry.put("by", userId);
                offer.getHistory().add(historyEntry);
                
                offerRepository.save(offer);
                
                return Map.of("message", "Counter offer rejected successfully");
            }
        } else {
            // This is a seller making a counter offer
            if (!offer.getSellerId().equals(userId)) {
                throw new RuntimeException("Only seller can counter offer");
            }

            if (offer.getStatus() != OfferStatus.PENDING) {
                throw new RuntimeException("Offer is not pending");
            }

            // Update offer with counter offer
            offer.setStatus(OfferStatus.COUNTERED);
            offer.setCounterAmount(request.getAmount());
            offer.setCounterMessage(request.getMessage());

            if (offer.getHistory() == null) {
                offer.setHistory(new ArrayList<>());
            }
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("action", "countered");
            historyEntry.put("amount", request.getAmount());
            historyEntry.put("message", request.getMessage());
            historyEntry.put("timestamp", LocalDateTime.now());
            historyEntry.put("by", userId);
            offer.getHistory().add(historyEntry);

            offerRepository.save(offer);

            return Map.of("message", "Counter offer sent successfully");
        }
        
        throw new RuntimeException("Invalid request");
    }

    public Map<String, Object> respondToCounterOffer(String offerId, CounterOfferResponseRequest request, String userId) {
        Offer offer = offerRepository.findByOfferId(offerId)
            .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!offer.getBuyerId().equals(userId)) {
            throw new RuntimeException("Only buyer can respond to counter offer");
        }
        
        if (offer.getStatus() != OfferStatus.COUNTERED) {
            throw new RuntimeException("No counter offer to respond to");
        }
        
        if ("accept".equals(request.getResponseType())) {
            // Buyer accepts counter offer
            offer.setStatus(OfferStatus.COUNTER_ACCEPTED);
            offer.setCounterResponse("accepted");
            offer.setCounterResponseMessage(request.getMessage());
            
            // Add to history
            if (offer.getHistory() == null) {
                offer.setHistory(new ArrayList<>());
            }
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("action", "counter_accepted");
            historyEntry.put("message", request.getMessage());
            historyEntry.put("timestamp", LocalDateTime.now());
            historyEntry.put("by", userId);
            offer.getHistory().add(historyEntry);
            
            offerRepository.save(offer);
            
            // Get both buyer and seller details for contact exchange
            User seller = userService.findByUserId(offer.getSellerId());
            User buyer = userService.findByUserId(offer.getBuyerId());
            
            return Map.of(
                "message", "Counter offer accepted successfully",
                "buyer_contact", Map.of(
                    "name", buyer.getName(),
                    "email", buyer.getEmail(),
                    "phone", buyer.getPhone()
                ),
                "seller_contact", Map.of(
                    "name", seller.getName(),
                    "email", seller.getEmail(),
                    "phone", seller.getPhone()
                )
            );
            
        } else if ("reject".equals(request.getResponseType())) {
            // Buyer rejects counter offer
            offer.setStatus(OfferStatus.COUNTER_REJECTED);
            offer.setCounterResponse("rejected");
            offer.setCounterResponseMessage(request.getMessage());
            
            // Add to history
            if (offer.getHistory() == null) {
                offer.setHistory(new ArrayList<>());
            }
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("action", "counter_rejected");
            historyEntry.put("message", request.getMessage());
            historyEntry.put("timestamp", LocalDateTime.now());
            historyEntry.put("by", userId);
            offer.getHistory().add(historyEntry);
            
            offerRepository.save(offer);
            
            return Map.of("message", "Counter offer rejected successfully");
        }
        
        throw new RuntimeException("Invalid response type");
    }

    public Map<String, Object> getReceivedOffers(String userId) {
        List<Offer> offers = offerRepository.findBySellerIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> offersWithDetails = new ArrayList<>();

        for (Offer offer : offers) {
            Map<String, Object> offerDetails = new HashMap<>();
            offerDetails.put("offer_id", offer.getOfferId());
            offerDetails.put("product_id", offer.getProductId());
            offerDetails.put("buyer_id", offer.getBuyerId());
            offerDetails.put("buyer_name", offer.getBuyerName());
            offerDetails.put("buyer_email", offer.getBuyerEmail());
            offerDetails.put("amount", offer.getAmount());
            offerDetails.put("message", offer.getMessage());
            offerDetails.put("status", offer.getStatus());
            offerDetails.put("counter_amount", offer.getCounterAmount());
            offerDetails.put("counter_message", offer.getCounterMessage());
            offerDetails.put("createdAt", offer.getCreatedAt());

            // Add product details
            productRepository.findByProductIdAndIsActiveTrue(offer.getProductId())
                .ifPresent(product -> offerDetails.put("product_name", product.getName()));

            offersWithDetails.add(offerDetails);
        }

        return Map.of("offers", offersWithDetails);
    }

    public Map<String, Object> getSentOffers(String userId) {
        List<Offer> offers = offerRepository.findByBuyerIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> offersWithDetails = new ArrayList<>();

        for (Offer offer : offers) {
            Map<String, Object> offerDetails = new HashMap<>();
            offerDetails.put("offer_id", offer.getOfferId());
            offerDetails.put("product_id", offer.getProductId());
            offerDetails.put("seller_id", offer.getSellerId());
            offerDetails.put("amount", offer.getAmount());
            offerDetails.put("message", offer.getMessage());
            offerDetails.put("status", offer.getStatus());
            offerDetails.put("counter_amount", offer.getCounterAmount());
            offerDetails.put("counter_message", offer.getCounterMessage());
            offerDetails.put("createdAt", offer.getCreatedAt());

            // Add product details
            productRepository.findByProductIdAndIsActiveTrue(offer.getProductId())
                .ifPresent(product -> offerDetails.put("product_name", product.getName()));

            // Add seller contact details for accepted offers
            if (offer.getStatus() == OfferStatus.ACCEPTED || offer.getStatus() == OfferStatus.COUNTER_ACCEPTED) {
                User seller = userService.findByUserId(offer.getSellerId());
                offerDetails.put("seller_contact", Map.of(
                    "name", seller.getName(),
                    "email", seller.getEmail(),
                    "phone", seller.getPhone()
                ));
            }

            offersWithDetails.add(offerDetails);
        }

        return Map.of("offers", offersWithDetails);
    }

    public Map<String, Object> getOffersForProduct(String productId, String userId) {
        // First verify the user owns this product
        Product product = productRepository.findByProductIdAndIsActiveTrue(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!product.getSellerId().equals(userId)) {
            throw new RuntimeException("Only product owner can view offers for this product");
        }

        List<Offer> offers = offerRepository.findByProductIdOrderByCreatedAtDesc(productId);
        List<Map<String, Object>> offersWithDetails = new ArrayList<>();

        for (Offer offer : offers) {
            Map<String, Object> offerDetails = new HashMap<>();
            offerDetails.put("id", offer.getOfferId());
            offerDetails.put("productId", offer.getProductId());
            offerDetails.put("buyerId", offer.getBuyerId());
            offerDetails.put("buyerName", offer.getBuyerName());
            offerDetails.put("buyerEmail", offer.getBuyerEmail());
            offerDetails.put("amount", offer.getAmount());
            offerDetails.put("message", offer.getMessage());
            offerDetails.put("status", offer.getStatus());
            offerDetails.put("counterAmount", offer.getCounterAmount());
            offerDetails.put("counterMessage", offer.getCounterMessage());
            offerDetails.put("createdAt", offer.getCreatedAt());

            offersWithDetails.add(offerDetails);
        }

        return Map.of("offers", offersWithDetails);
    }
}
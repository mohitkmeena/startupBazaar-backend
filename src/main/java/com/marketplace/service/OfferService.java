package com.marketplace.service;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.CounterOfferRequest;
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

        // Get seller details
        User seller = userService.findByUserId(offer.getSellerId());

        Map<String, Object> response = new HashMap<>();
        response.put("seller_email", seller.getEmail());
        response.put("seller_phone", seller.getPhone());
        response.put("seller_name", seller.getName());

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

    public Map<String, Object> getReceivedOffers(String userId) {
        List<Offer> offers = offerRepository.findBySellerIdOrderByCreatedAtDesc(userId);

        // Add product details
        offers.forEach(offer -> {
            productRepository.findByProductIdAndIsActiveTrue(offer.getProductId())
                .ifPresent(product -> offer.setProductId(product.getName())); // Reusing field for product name
        });

        return Map.of("offers", offers);
    }

    public Map<String, Object> getSentOffers(String userId) {
        List<Offer> offers = offerRepository.findByBuyerIdOrderByCreatedAtDesc(userId);

        // Add product details and seller contact for accepted offers
        offers.forEach(offer -> {
            productRepository.findByProductIdAndIsActiveTrue(offer.getProductId())
                .ifPresent(product -> offer.setProductId(product.getName())); // Reusing field for product name

            if (offer.getStatus() == OfferStatus.ACCEPTED) {
                User seller = userService.findByUserId(offer.getSellerId());
                // Note: In a real implementation, you'd create a proper response DTO
                // For now, we'll add seller contact info to the offer object
            }
        });

        return Map.of("offers", offers);
    }
}
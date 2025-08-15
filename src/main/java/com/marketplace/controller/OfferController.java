package com.marketplace.controller;

import com.marketplace.dto.ApiResponse;
import com.marketplace.dto.CounterOfferRequest;
import com.marketplace.dto.OfferCreateRequest;
import com.marketplace.security.UserPrincipal;
import com.marketplace.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<?> createOffer(@Valid @RequestBody OfferCreateRequest request,
                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Map<String, Object> response = offerService.createOffer(request, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Only buyers")) {
                return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{offerId}/accept")
    public ResponseEntity<?> acceptOffer(@PathVariable String offerId,
                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Map<String, Object> response = offerService.acceptOffer(offerId, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
            }
            if (e.getMessage().contains("Only seller")) {
                return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{offerId}/reject")
    public ResponseEntity<?> rejectOffer(@PathVariable String offerId,
                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Map<String, Object> response = offerService.rejectOffer(offerId, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
            }
            if (e.getMessage().contains("Only seller")) {
                return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{offerId}/counter")
    public ResponseEntity<?> counterOffer(@PathVariable String offerId,
                                                   @Valid @RequestBody CounterOfferRequest request,
                                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Map<String, Object> response = offerService.counterOffer(offerId, request, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
            }
            if (e.getMessage().contains("Only seller")) {
                return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/received")
    public ResponseEntity<?> getReceivedOffers(@AuthenticationPrincipal UserPrincipal currentUser) {
        Map<String, Object> response = offerService.getReceivedOffers(currentUser.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<?> getSentOffers(@AuthenticationPrincipal UserPrincipal currentUser) {
        Map<String, Object> response = offerService.getSentOffers(currentUser.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getOffersForProduct(@PathVariable String productId,
                                               @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            Map<String, Object> response = offerService.getOffersForProduct(productId, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
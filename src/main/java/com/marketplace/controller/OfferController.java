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

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createOffer(@Valid @RequestBody OfferCreateRequest request,
                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            ApiResponse response = offerService.createOffer(request, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Only buyers")) {
                return ResponseEntity.status(403).body(ApiResponse.success(e.getMessage()));
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.success(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.success(e.getMessage()));
        }
    }

    @PostMapping("/{offerId}/accept")
    public ResponseEntity<ApiResponse> acceptOffer(@PathVariable String offerId,
                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            ApiResponse response = offerService.acceptOffer(offerId, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.success(e.getMessage()));
            }
            if (e.getMessage().contains("Only seller")) {
                return ResponseEntity.status(403).body(ApiResponse.success(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.success(e.getMessage()));
        }
    }

    @PostMapping("/{offerId}/reject")
    public ResponseEntity<ApiResponse> rejectOffer(@PathVariable String offerId,
                                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            ApiResponse response = offerService.rejectOffer(offerId, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.success(e.getMessage()));
            }
            if (e.getMessage().contains("Only seller")) {
                return ResponseEntity.status(403).body(ApiResponse.success(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.success(e.getMessage()));
        }
    }

    @PostMapping("/{offerId}/counter")
    public ResponseEntity<ApiResponse> counterOffer(@PathVariable String offerId,
                                                   @Valid @RequestBody CounterOfferRequest request,
                                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            ApiResponse response = offerService.counterOffer(offerId, request, currentUser.getUserId());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(ApiResponse.success(e.getMessage()));
            }
            if (e.getMessage().contains("Only seller")) {
                return ResponseEntity.status(403).body(ApiResponse.success(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.success(e.getMessage()));
        }
    }

    @GetMapping("/received")
    public ResponseEntity<ApiResponse> getReceivedOffers(@AuthenticationPrincipal UserPrincipal currentUser) {
        ApiResponse response = offerService.getReceivedOffers(currentUser.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<ApiResponse> getSentOffers(@AuthenticationPrincipal UserPrincipal currentUser) {
        ApiResponse response = offerService.getSentOffers(currentUser.getUserId());
        return ResponseEntity.ok(response);
    }
}
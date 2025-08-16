package com.marketplace.service;

import com.marketplace.dto.CounterOfferRequest;
import com.marketplace.enums.OfferStatus;
import com.marketplace.enums.UserRole;
import com.marketplace.model.Offer;
import com.marketplace.model.Product;
import com.marketplace.model.User;
import com.marketplace.repository.OfferRepository;
import com.marketplace.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OfferService offerService;

    private User buyer;
    private User seller;
    private Product product;
    private Offer offer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        buyer = new User("buyer123", "John Buyer", "buyer@test.com", "password", "1234567890", UserRole.BUYER, "New York");
        seller = new User("seller123", "Jane Seller", "seller@test.com", "password", "9876543210", UserRole.SELLER, "Los Angeles");
        product = new Product();
        product.setProductId("product123");
        product.setSellerId("seller123");
        product.setActive(true);

        offer = new Offer();
        offer.setOfferId("offer123");
        offer.setProductId("product123");
        offer.setBuyerId("buyer123");
        offer.setSellerId("seller123");
        offer.setAmount(1000.0);
        offer.setStatus(OfferStatus.PENDING);
    }

    @Test
    void testAcceptOffer() {
        // Arrange
        when(offerRepository.findByOfferId("offer123")).thenReturn(Optional.of(offer));
        when(userService.findByUserId("seller123")).thenReturn(seller);
        when(userService.findByUserId("buyer123")).thenReturn(buyer);
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        // Act
        var result = offerService.acceptOffer("offer123", "seller123");

        // Assert
        assertNotNull(result);
        assertEquals("Offer accepted successfully", result.get("message"));
        assertNotNull(result.get("buyer_contact"));
        assertNotNull(result.get("seller_contact"));
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void testRejectOffer() {
        // Arrange
        when(offerRepository.findByOfferId("offer123")).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        // Act
        var result = offerService.rejectOffer("offer123", "seller123");

        // Assert
        assertNotNull(result);
        assertEquals("Offer rejected successfully", result.get("message"));
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void testCounterOffer() {
        // Arrange
        when(offerRepository.findByOfferId("offer123")).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        CounterOfferRequest request = new CounterOfferRequest();
        request.setAmount(1200.0);
        request.setMessage("How about this price?");

        // Act
        var result = offerService.counterOffer("offer123", request, "seller123");

        // Assert
        assertNotNull(result);
        assertEquals("Counter offer sent successfully", result.get("message"));
        verify(offerRepository).save(any(Offer.class));
    }
} 
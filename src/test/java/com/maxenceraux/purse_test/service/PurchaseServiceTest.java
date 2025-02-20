package com.maxenceraux.purse_test.service;

import com.maxenceraux.purse_test.exception.MissingPurchaseException;
import com.maxenceraux.purse_test.exception.UnsupportedMethodChangeException;
import com.maxenceraux.purse_test.exception.UnsupportedStatusChangeException;
import com.maxenceraux.purse_test.factory.PurchaseFactory;
import com.maxenceraux.purse_test.model.PaymentMethod;
import com.maxenceraux.purse_test.model.PaymentStatus;
import com.maxenceraux.purse_test.model.Purchase;
import com.maxenceraux.purse_test.model.PurchasedProduct;
import com.maxenceraux.purse_test.repository.PurchaseRepository;
import com.maxenceraux.purse_test.repository.PurchasedProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PurchaseServiceTest {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchasedProductRepository purchasedProductRepository;

    @Autowired
    private PurchaseService service;

    @BeforeEach
    void init() {
        var savedProduct1 = PurchaseFactory.buildProduct("name", "ref", 3, BigDecimal.valueOf(33.1), 1L);
        var savedProduct2 = PurchaseFactory.buildProduct("name2", "ref2", 2, BigDecimal.valueOf(12.1), 1L);
        var savedPurchase = PurchaseFactory.buildPurchase(BigDecimal.valueOf(123.5), null);
        var savedProduct3 = PurchaseFactory.buildProduct("name3", "ref3", 4, BigDecimal.valueOf(3.1), 2L);
        var savedPurchase2 = PurchaseFactory.buildPurchase(BigDecimal.valueOf(12.4), null);

        purchaseRepository.saveAll(List.of(savedPurchase, savedPurchase2)).blockLast();
        purchasedProductRepository.saveAll(List.of(savedProduct1, savedProduct2, savedProduct3)).blockLast();
    }

    @Test
    @DisplayName("Should save purchase properly and return it.")
    void shouldSavePurchaseProperlyAndReturnIt() {
        var product1 = PurchaseFactory.buildProduct("name", "ref", 3, BigDecimal.valueOf(33.1), null);
        var product2 = PurchaseFactory.buildProduct( "name", "ref", 2, BigDecimal.valueOf(12.1), null);
        var purchaseToSave = new Purchase(
                null,
                null,
                "EUR",
                PaymentMethod.CREDIT_CARD,
                null,
                List.of(product1, product2));

        var expectedProduct1 = new PurchasedProduct(4L, "name", "ref", 3, BigDecimal.valueOf(33.1), 3L);
        var expectedProduct2 = new PurchasedProduct(5L, "name", "ref", 2, BigDecimal.valueOf(12.1), 3L);
        var expectedPurchase = new Purchase(
                3L,
                BigDecimal.valueOf(123.5),
                "EUR",
                PaymentMethod.CREDIT_CARD,
                PaymentStatus.IN_PROGRESS,
                List.of(expectedProduct1, expectedProduct2));

        StepVerifier.withVirtualTime(() -> service.createPurchase(purchaseToSave))
                .expectNext(expectedPurchase)
                .verifyComplete();

        StepVerifier.withVirtualTime(() -> purchaseRepository.findById(3L))
                .expectNext(expectedPurchase.withPurchasedProducts(null))
                .verifyComplete();

        StepVerifier.withVirtualTime(() -> purchasedProductRepository.findAllByPurchaseId(3L))
                .expectNext(expectedProduct1, expectedProduct2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find all purchases.")
    void shouldFindAllPurchases() {
        var expectedProduct1 = PurchaseFactory.buildProductWithId(1L, "name", "ref", 3, BigDecimal.valueOf(33.1), 1L);
        var expectedProduct2 = PurchaseFactory.buildProductWithId(2L, "name2", "ref2", 2, BigDecimal.valueOf(12.1), 1L);
        var expectedPurchase = PurchaseFactory.buildPurchaseWithId(1L, BigDecimal.valueOf(123.5), List.of(expectedProduct1, expectedProduct2));

        var expectedProduct3 = PurchaseFactory.buildProductWithId(3L, "name3", "ref3", 4, BigDecimal.valueOf(3.1), 2L);
        var expectedPurchase2 = PurchaseFactory.buildPurchaseWithId(2L, BigDecimal.valueOf(12.4), List.of(expectedProduct3));


        StepVerifier.withVirtualTime(() -> service.findAllPurchases())
                .expectNext(expectedPurchase, expectedPurchase2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should find one purchase.")
    void shouldFindOnePurchase() {
        var expectedProduct = PurchaseFactory.buildProductWithId(3L, "name3", "ref3", 4, BigDecimal.valueOf(3.1), 2L);
        var expectedPurchase = PurchaseFactory.buildPurchaseWithId(2L, BigDecimal.valueOf(12.4), List.of(expectedProduct));

        StepVerifier.withVirtualTime(() -> service.findById(2L))
                .expectNext(expectedPurchase)
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
            "IN_PROGRESS, AUTHORIZED",
            "AUTHORIZED, CAPTURED"
    })
    @DisplayName("Should update status.")
    void shouldUpdateStatus(PaymentStatus baseStatus, PaymentStatus newStatus) {
        var savedProduct = PurchaseFactory.buildDefaultProduct();
        var savedPurchase = PurchaseFactory.buildDefaultPurchase();
        savedPurchase.setStatus(baseStatus);

        var expectedProduct = PurchaseFactory.buildDefaultPurchase().withPurchasedProducts(List.of(savedProduct));
        expectedProduct.setStatus(newStatus);
        expectedProduct.setId(3L);

        purchaseRepository.save(savedPurchase).block();
        purchasedProductRepository.save(savedProduct).block();

        StepVerifier.withVirtualTime(() -> service.updateStatus(3L, newStatus))
                .expectNext(expectedProduct)
                .verifyComplete();

        StepVerifier.withVirtualTime(() -> purchaseRepository.findById(3L))
                .expectNext(expectedProduct.withPurchasedProducts(null))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should not update status on unknown purchase.")
    void shouldNotUpdateStatusOnUnknownPurchase() {
        StepVerifier.withVirtualTime(() -> service.updateStatus(3L, PaymentStatus.CAPTURED))
                .expectError(MissingPurchaseException.class)
                .verify();
    }

    @ParameterizedTest
    @CsvSource({
            "IN_PROGRESS, CAPTURED",
            "AUTHORIZED, IN_PROGRESS",
            "CAPTURED, IN_PROGRESS",
            "CAPTURED, AUTHORIZED",
            "IN_PROGRESS, IN_PROGRESS",
            "AUTHORIZED, AUTHORIZED",
            "CAPTURED, CAPTURED"
    })
    @DisplayName("Should not update on invalid status change.")
    void shouldNotUpdateOnInvalidStatusChange(PaymentStatus baseStatus, PaymentStatus newStatus) {
        var savedProduct = PurchaseFactory.buildDefaultProduct();
        var savedPurchase = PurchaseFactory.buildDefaultPurchase();
        savedPurchase.setStatus(baseStatus);

        purchaseRepository.save(savedPurchase).block();
        purchasedProductRepository.save(savedProduct).block();

        StepVerifier.withVirtualTime(() -> service.updateStatus(3L, newStatus))
                .expectError(UnsupportedStatusChangeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should update payment method.")
    void shouldUpdatePaymentMethod() {
        var savedProduct = PurchaseFactory.buildDefaultProduct();
        var savedPurchase = PurchaseFactory.buildDefaultPurchase();

        var expectedProduct = PurchaseFactory.buildDefaultPurchase().withPurchasedProducts(List.of(savedProduct));
        expectedProduct.setPaymentMethod(PaymentMethod.PAYPAL);
        expectedProduct.setId(3L);

        purchaseRepository.save(savedPurchase).block();
        purchasedProductRepository.save(savedProduct).block();

        StepVerifier.withVirtualTime(() -> service.changePaymentMethod(3L, PaymentMethod.PAYPAL))
                .expectNext(expectedProduct)
                .verifyComplete();

        StepVerifier.withVirtualTime(() -> purchaseRepository.findById(3L))
                .expectNext(expectedProduct.withPurchasedProducts(null))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should not change method on missing purchase.")
    void shouldNotChangeMethodOnMissingPurchase() {
        StepVerifier.withVirtualTime(() -> service.changePaymentMethod(3L, PaymentMethod.PAYPAL))
                .expectError(MissingPurchaseException.class)
                .verify();
    }

    @ParameterizedTest
    @CsvSource({
            "AUTHORIZED",
            "CAPTURED"
    })
    @DisplayName("Should not change method on invalid status.")
    void shouldNotChangeMethodOnInvalidStatus(PaymentStatus status) {
        var savedProduct = PurchaseFactory.buildDefaultProduct();
        var savedPurchase = PurchaseFactory.buildDefaultPurchase();
        savedPurchase.setStatus(status);

        purchaseRepository.save(savedPurchase).block();
        purchasedProductRepository.save(savedProduct).block();

        StepVerifier.withVirtualTime(() -> service.changePaymentMethod(3L, PaymentMethod.PAYPAL))
                .expectError(UnsupportedMethodChangeException.class)
                .verify();
    }
}
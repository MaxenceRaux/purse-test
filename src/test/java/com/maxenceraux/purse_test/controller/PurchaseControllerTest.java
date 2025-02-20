package com.maxenceraux.purse_test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxenceraux.purse_test.config.ConversionServiceConfig;
import com.maxenceraux.purse_test.exception.MissingPurchaseException;
import com.maxenceraux.purse_test.exception.UnsupportedMethodChangeException;
import com.maxenceraux.purse_test.exception.UnsupportedStatusChangeException;
import com.maxenceraux.purse_test.factory.PurchaseFactory;
import com.maxenceraux.purse_test.model.*;
import com.maxenceraux.purse_test.service.PurchaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebFluxTest(PurchaseController.class)
@Import({ConversionServiceConfig.class})
class PurchaseControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ConversionService conversionService;

    @MockitoBean
    private PurchaseService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Should fetch purchase by id.")
    void shouldFetchPurchaseById() throws JsonProcessingException {
        var product = PurchaseFactory.buildDefaultProduct();
        var purchase = PurchaseFactory.buildPurchaseWithId(1L, BigDecimal.valueOf(12.4), List.of(product));

        var productDTO = new PurchasedProductDTO("name", "ref", 4, BigDecimal.valueOf(3.1));
        var purchaseDTO = new PurchaseDTO(1L, BigDecimal.valueOf(12.4), "EUR", PaymentMethod.CREDIT_CARD, PaymentStatus.IN_PROGRESS, List.of(productDTO));

        when(service.findById(1L)).thenReturn(Mono.just(purchase));

        webTestClient.get()
                .uri("/api/purchase/1")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .json(mapper.writeValueAsString(purchaseDTO));
    }

    @Test
    @DisplayName("Should return 404 on missing purchase.")
    void shouldReturn404OnMissingPurchase() {
        when(service.findById(1L)).thenReturn(Mono.error(new MissingPurchaseException(1L)));

        webTestClient.get()
                .uri("/api/purchase/1")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("Should find all purchases.")
    void shouldFindAllPurchases() throws JsonProcessingException {
        var product = PurchaseFactory.buildDefaultProduct();
        var purchase = PurchaseFactory.buildPurchaseWithId(1L, BigDecimal.valueOf(12.4), List.of(product));

        var product2 = PurchaseFactory.buildProduct("name2", "ref2", 3, BigDecimal.valueOf(10.1), 2L);
        var purchase2 = PurchaseFactory.buildPurchaseWithId(2L, BigDecimal.valueOf(30.3), List.of(product2));

        var productDTO = new PurchasedProductDTO("name", "ref", 4, BigDecimal.valueOf(3.1));
        var purchaseDTO = new PurchaseDTO(1L, BigDecimal.valueOf(12.4), "EUR", PaymentMethod.CREDIT_CARD, PaymentStatus.IN_PROGRESS, List.of(productDTO));
        var productDTO2 = new PurchasedProductDTO("name2", "ref2", 3, BigDecimal.valueOf(10.1));
        var purchaseDTO2 = new PurchaseDTO(2L, BigDecimal.valueOf(30.3), "EUR", PaymentMethod.CREDIT_CARD, PaymentStatus.IN_PROGRESS, List.of(productDTO2));

        when(service.findAllPurchases()).thenReturn(Flux.just(purchase, purchase2));

        webTestClient.get()
                .uri("/api/purchase/all")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .json(mapper.writeValueAsString(List.of(purchaseDTO, purchaseDTO2)));
    }

    @Test
    @DisplayName("Should create purchase.")
    void shouldCreatePurchase() throws JsonProcessingException {
        var productDTO = new PurchasedProductDTO("name", "ref", 4, BigDecimal.valueOf(3.1));
        var purchaseDTO = new PurchaseDTO(null, null, "EUR", PaymentMethod.CREDIT_CARD, null, List.of(productDTO));

        var productIn = new PurchasedProduct(null, "name", "ref", 4, BigDecimal.valueOf(3.1), null);
        var purchaseIn = new Purchase(null, null, "EUR", PaymentMethod.CREDIT_CARD, null, List.of(productIn));

        var productOut = new PurchasedProduct(1L, "name", "ref", 4, BigDecimal.valueOf(3.1), 1L);
        var purchaseOut = new Purchase(1L, BigDecimal.valueOf(12.4), "EUR", PaymentMethod.CREDIT_CARD, PaymentStatus.IN_PROGRESS, List.of(productOut));

        var productDTOOut = new PurchasedProductDTO("name", "ref", 4, BigDecimal.valueOf(3.1));
        var purchaseDTOOut = new PurchaseDTO(1L, BigDecimal.valueOf(12.4), "EUR", PaymentMethod.CREDIT_CARD, PaymentStatus.IN_PROGRESS, List.of(productDTOOut));

        when(service.createPurchase(purchaseIn))
                .thenReturn(Mono.just(purchaseOut));

        webTestClient.post()
                .uri("/api/purchase")
                .bodyValue(purchaseDTO)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .json(mapper.writeValueAsString(purchaseDTOOut));
    }

    @Test
    @DisplayName("Should patch payment method.")
    void shouldPatchPaymentMethod() throws JsonProcessingException {
        var productOut = new PurchasedProduct(1L, "name", "ref", 4, BigDecimal.valueOf(3.1), 1L);
        var purchaseOut = new Purchase(1L, BigDecimal.valueOf(12.4), "EUR", PaymentMethod.PAYPAL, PaymentStatus.IN_PROGRESS, List.of(productOut));

        var productDTOOut = new PurchasedProductDTO("name", "ref", 4, BigDecimal.valueOf(3.1));
        var purchaseDTOOut = new PurchaseDTO(1L, BigDecimal.valueOf(12.4), "EUR", PaymentMethod.PAYPAL, PaymentStatus.IN_PROGRESS, List.of(productDTOOut));


        when(service.changePaymentMethod(1L, PaymentMethod.PAYPAL))
                .thenReturn(Mono.just(purchaseOut));

        webTestClient.patch()
                .uri("/api/purchase/1/paymentMethod")
                .bodyValue(PaymentMethod.PAYPAL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(mapper.writeValueAsString(purchaseDTOOut));
    }

    @Test
    @DisplayName("Should return 404 on missing purchase (method).")
    void shouldReturn404OnMissingPurchaseForMethod() {
        when(service.changePaymentMethod(1L, PaymentMethod.PAYPAL))
                .thenReturn(Mono.error(new MissingPurchaseException(1L)));

        webTestClient.patch()
                .uri("/api/purchase/1/paymentMethod")
                .bodyValue(PaymentMethod.PAYPAL)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("Should return 400 for invalid method change.")
    void shouldReturn400ForInvalidMethodChange() {
        when(service.changePaymentMethod(1L, PaymentMethod.PAYPAL))
                .thenReturn(Mono.error(new UnsupportedMethodChangeException()));

        webTestClient.patch()
                .uri("/api/purchase/1/paymentMethod")
                .bodyValue(PaymentMethod.PAYPAL)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("Should patch status.")
    void shouldPatchStatus() throws JsonProcessingException {
        var productOut = new PurchasedProduct(1L, "name", "ref", 4, BigDecimal.valueOf(3.1), 1L);
        var purchaseOut = new Purchase(1L, BigDecimal.valueOf(12.4), "EUR", PaymentMethod.PAYPAL, PaymentStatus.CAPTURED, List.of(productOut));

        var productDTOOut = new PurchasedProductDTO("name", "ref", 4, BigDecimal.valueOf(3.1));
        var purchaseDTOOut = new PurchaseDTO(1L, BigDecimal.valueOf(12.4), "EUR", PaymentMethod.PAYPAL, PaymentStatus.CAPTURED, List.of(productDTOOut));


        when(service.updateStatus(1L, PaymentStatus.CAPTURED))
                .thenReturn(Mono.just(purchaseOut));

        webTestClient.patch()
                .uri("/api/purchase/1/status")
                .bodyValue(PaymentStatus.CAPTURED)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json(mapper.writeValueAsString(purchaseDTOOut));
    }

    @Test
    @DisplayName("Should return 404 for missing purchase (patch status).")
    void shouldReturn404ForMissingPurchasePatchStatus() {
        when(service.updateStatus(1L, PaymentStatus.CAPTURED))
                .thenReturn(Mono.error(new MissingPurchaseException(1L)));

        webTestClient.patch()
                .uri("/api/purchase/1/status")
                .bodyValue(PaymentStatus.CAPTURED)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @DisplayName("Should return 400 for invalid status change.")
    void shouldReturn404ForInvalidStatusChange() {
        when(service.updateStatus(1L, PaymentStatus.CAPTURED))
                .thenReturn(Mono.error(new UnsupportedStatusChangeException(PaymentStatus.IN_PROGRESS, PaymentStatus.CAPTURED)));

        webTestClient.patch()
                .uri("/api/purchase/1/status")
                .bodyValue(PaymentStatus.CAPTURED)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

}
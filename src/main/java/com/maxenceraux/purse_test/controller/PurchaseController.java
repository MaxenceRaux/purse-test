package com.maxenceraux.purse_test.controller;

import com.maxenceraux.purse_test.model.PaymentMethod;
import com.maxenceraux.purse_test.model.PaymentStatus;
import com.maxenceraux.purse_test.model.Purchase;
import com.maxenceraux.purse_test.model.PurchaseDTO;
import com.maxenceraux.purse_test.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase")
public class PurchaseController {

	private final PurchaseService purchaseService;

	private final ConversionService conversionService;

	@Operation(summary = "Get all purchase stored")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found some purchase to return",
					content = { @Content(mediaType = "application/json",
							array = @ArraySchema(arraySchema = @Schema(implementation = PurchaseDTO.class))) })})
	@GetMapping("/all")
	@ResponseStatus(HttpStatus.OK)
	public Flux<PurchaseDTO> findAll() {
		return purchaseService.findAllPurchases()
				.map(purchase -> conversionService.convert(purchase, PurchaseDTO.class));
	}

	@Operation(summary = "Get a purchase from its id.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Found the purchase",
					content = { @Content(mediaType = "application/json",
							schema = @Schema(implementation = PurchaseDTO.class)) }),
			@ApiResponse(responseCode = "404", description = "Purchase not found",
					content = @Content) })
	@GetMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<PurchaseDTO> findById(@PathVariable("id") Long id) {
		return purchaseService.findById(id)
				.map(purchase -> conversionService.convert(purchase, PurchaseDTO.class));
	}

	@Operation(summary = "Create a purchase.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created the purchase",
					content = { @Content(mediaType = "application/json",
							schema = @Schema(implementation = PurchaseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid purchase supplied",
					content = @Content) })
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<PurchaseDTO> createOne(@RequestBody @Valid @NotNull PurchaseDTO purchase) {
		return purchaseService.createPurchase(Objects.requireNonNull(conversionService.convert(purchase, Purchase.class)))
				.map(savedPurchase -> conversionService.convert(savedPurchase, PurchaseDTO.class));
	}

	@Operation(summary = "Update a purchase status.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Updated the purchase",
					content = { @Content(mediaType = "application/json",
							schema = @Schema(implementation = PurchaseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid status provided",
					content = @Content),
			@ApiResponse(responseCode = "404", description = "Purchase to update has not been found",
					content = @Content) })
	@PatchMapping("/{id}/status")
	@ResponseStatus(HttpStatus.OK)
	public Mono<PurchaseDTO> updateStatus(@PathVariable Long id, @RequestBody PaymentStatus status) {
		return purchaseService.updateStatus(id, status)
				.map(purchase -> conversionService.convert(purchase, PurchaseDTO.class));
	}

	@Operation(summary = "Update a purchase payment method.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Updated the purchase",
					content = { @Content(mediaType = "application/json",
							schema = @Schema(implementation = PurchaseDTO.class)) }),
			@ApiResponse(responseCode = "400", description = "Invalid payment method provided or wrong status to proceed",
					content = @Content),
			@ApiResponse(responseCode = "404", description = "Purchase to update has not been found",
					content = @Content) })
	@PatchMapping("/{id}/paymentMethod")
	@ResponseStatus(HttpStatus.OK)
	public Mono<PurchaseDTO> changePaymentMethod(@PathVariable Long id, @RequestBody PaymentMethod paymentMethod) {
		return purchaseService.changePaymentMethod(id, paymentMethod)
				.map(purchase -> conversionService.convert(purchase, PurchaseDTO.class));
	}

}

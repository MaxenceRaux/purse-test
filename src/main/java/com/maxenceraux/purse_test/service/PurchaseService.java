package com.maxenceraux.purse_test.service;

import com.maxenceraux.purse_test.exception.MissingPurchaseException;
import com.maxenceraux.purse_test.model.PaymentMethod;
import com.maxenceraux.purse_test.model.PaymentStatus;
import com.maxenceraux.purse_test.model.Purchase;
import com.maxenceraux.purse_test.repository.PurchaseRepository;
import com.maxenceraux.purse_test.repository.PurchasedProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PurchaseService {

	private final PurchaseRepository purchaseRepository;
	private final PurchasedProductRepository purchasedProductRepository;

	public Flux<Purchase> findAllPurchases() {
		return purchaseRepository.findAll()
				.flatMap(purchase ->
						purchasedProductRepository.findAllByPurchaseId(purchase.getId())
						.collectList()
						.map(purchase::withPurchasedProducts));
	}
	
	public Mono<Purchase> findPurchaseById(Long purchaseId) {
		return Mono.zip(
				purchaseRepository.findById(purchaseId),
				purchasedProductRepository.findAllByPurchaseId(purchaseId).collectList(),
                Purchase::withPurchasedProducts);
	}
	
	public Mono<Purchase> createPurchase(Purchase purchase) {
		purchase.initPurchase();
		return purchaseRepository.save(purchase)
				.flatMap(savedPurchase -> {
					var purchasedProducts = purchase.getPurchasedProducts();
					purchasedProducts.forEach(purchasedProduct -> purchasedProduct.setPurchaseId(savedPurchase.getId()));
					return purchasedProductRepository.saveAll(purchasedProducts)
							.collectList()
							.map(savedPurchase::withPurchasedProducts);
				});
	}

	public Mono<Purchase> updateStatus(Long purchaseId, PaymentStatus status) {
		return findPurchaseById(purchaseId)
				.switchIfEmpty(Mono.error(new MissingPurchaseException(purchaseId)))
				.map(purchase -> {
					purchase.updateStatus(status);
					return purchase;
				})
				.flatMap(purchaseRepository::save);
	}

	public Mono<Purchase> changePaymentMethod(Long purchaseId, PaymentMethod paymentMethod) {
		return findPurchaseById(purchaseId)
				.switchIfEmpty(Mono.error(new MissingPurchaseException(purchaseId)))
				.map(purchase -> {
					purchase.updatePaymentMethod(paymentMethod);
					return purchase;
				})
				.flatMap(purchaseRepository::save);
	}
}

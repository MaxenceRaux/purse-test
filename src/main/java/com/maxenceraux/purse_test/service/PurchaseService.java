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
	
	public Mono<Purchase> findById(Long id) {
		return Mono.zip(
				purchaseRepository.findById(id),
				purchasedProductRepository.findAllByPurchaseId(id).collectList(),
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

	public Mono<Purchase> updateStatus(Long id, PaymentStatus status) {
		return findById(id)
				.switchIfEmpty(Mono.error(new MissingPurchaseException(id)))
				.map(purchase -> {
					purchase.updateStatus(status);
					return purchase;
				})
				.flatMap(purchaseRepository::save);
	}

	public Mono<Purchase> changePaymentMethod(Long id, PaymentMethod paymentMethod) {
		return findById(id)
				.switchIfEmpty(Mono.error(new MissingPurchaseException(id)))
				.map(purchase -> {
					purchase.updatePaymentMethod(paymentMethod);
					return purchase;
				})
				.flatMap(purchaseRepository::save);
	}
}

package com.maxenceraux.purse_test.repository;

import com.maxenceraux.purse_test.model.PurchasedProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PurchasedProductRepository extends ReactiveCrudRepository<PurchasedProduct, Long> {

    Flux<PurchasedProduct> findAllByPurchaseId(Long id);
}

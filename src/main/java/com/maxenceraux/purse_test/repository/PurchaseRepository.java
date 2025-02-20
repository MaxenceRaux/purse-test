package com.maxenceraux.purse_test.repository;

import com.maxenceraux.purse_test.model.Purchase;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface PurchaseRepository extends R2dbcRepository<Purchase, Long> {

}

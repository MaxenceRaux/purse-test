package com.maxenceraux.purse_test.factory;

import com.maxenceraux.purse_test.model.PaymentMethod;
import com.maxenceraux.purse_test.model.PaymentStatus;
import com.maxenceraux.purse_test.model.Purchase;
import com.maxenceraux.purse_test.model.PurchasedProduct;

import java.math.BigDecimal;
import java.util.List;

public class PurchaseFactory {

    public static Purchase buildDefaultPurchase() {
        return buildPurchase(BigDecimal.valueOf(12.4), null);
    }

    public static Purchase buildPurchase(BigDecimal amount, List<PurchasedProduct> purchasedProducts) {
        return new Purchase(
                null,
                amount,
                "EUR",
                PaymentMethod.CREDIT_CARD,
                PaymentStatus.IN_PROGRESS,
                purchasedProducts);
    }

    public static Purchase buildPurchaseWithId(Long id, BigDecimal amount, List<PurchasedProduct> purchasedProducts) {
        return new Purchase(
                id,
                amount,
                "EUR",
                PaymentMethod.CREDIT_CARD,
                PaymentStatus.IN_PROGRESS,
                purchasedProducts);
    }

    public static PurchasedProduct buildDefaultProduct() {
        return buildProduct("name", "ref", 4, BigDecimal.valueOf(3.1), 3L);
    }

    public static PurchasedProduct buildProduct(String name, String reference, Integer quantity, BigDecimal price, Long purchaseId) {
        return new PurchasedProduct(null, name, reference, quantity, price, purchaseId);
    }

    public static PurchasedProduct buildProductWithId(Long id, String name, String reference, Integer quantity, BigDecimal price, Long purchaseId) {
        return new PurchasedProduct(id, name, reference, quantity, price, purchaseId);
    }
}

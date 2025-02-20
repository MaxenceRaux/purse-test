package com.maxenceraux.purse_test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("PURCHASED_PRODUCT")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PurchasedProduct {

    @Id
    private Long id;

    private String name;

    private String reference;

    private Integer quantity;

    private BigDecimal price;

    private Long purchaseId;

    public BigDecimal getTotalCost() {
        return price.multiply(new BigDecimal(quantity));
    }
}

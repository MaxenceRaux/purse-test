package com.maxenceraux.purse_test.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("PURCHASED_PRODUCT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasedProductDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String reference;

    @Positive
    private Integer quantity;

    @Positive
    private BigDecimal price;
}

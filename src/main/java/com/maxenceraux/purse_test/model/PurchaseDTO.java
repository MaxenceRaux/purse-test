package com.maxenceraux.purse_test.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDTO {

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private BigDecimal amount;

	@NotBlank
	private String currency;

	@NotNull
	private PaymentMethod paymentMethod;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private PaymentStatus status;

	@NotEmpty
	private List<@Valid PurchasedProductDTO> purchasedProducts;

}

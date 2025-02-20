package com.maxenceraux.purse_test.model;

import com.maxenceraux.purse_test.exception.UnsupportedMethodChangeException;
import com.maxenceraux.purse_test.exception.UnsupportedStatusChangeException;
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
public class Purchase {

	@Id
	private Long id;

	private BigDecimal amount;

	private String currency;

	@Column("payment_method")
	private PaymentMethod paymentMethod;

	private PaymentStatus status;

	@With
	@Transient
	private List<PurchasedProduct> purchasedProducts;

	public void initPurchase() {
		status = PaymentStatus.IN_PROGRESS;
		amount = purchasedProducts.stream()
				.map(PurchasedProduct::getTotalCost)
				.reduce(BigDecimal::add)
				.orElseThrow();
	}

	public void updateStatus(PaymentStatus status) throws UnsupportedStatusChangeException {
		if (!status.equals(this.status.next())) {
			throw new UnsupportedStatusChangeException(this.status, status);
		}
		this.status = status;
	}

	public void updatePaymentMethod(PaymentMethod paymentMethod) {
		if (!status.equals(PaymentStatus.IN_PROGRESS)) {
			throw new UnsupportedMethodChangeException();
		}
		this.paymentMethod = paymentMethod;
	}
}

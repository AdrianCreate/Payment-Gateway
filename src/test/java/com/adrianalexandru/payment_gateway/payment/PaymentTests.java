package com.adrianalexandru.payment_gateway.payment;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentTests {
	
	private Payment newPayment() {
		return new Payment("order_1", "customer_1", "4242", 1000);
	}
	
	@Test
	void authorizeMovesPendingToAuthorized() {
		Payment payment = newPayment();
		
		payment.authorize("auth_123");
		
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.AUTHORIZED);
		assertThat(payment.getBankAuthorizationId()).isEqualTo("auth_123");
		assertThat(payment.getAuthorizedAt()).isNotNull();
	}
	
	@Test
	void captureAfterAuthorizeStoresCaptureId() {
		Payment payment = newPayment();
		
		payment.authorize("auth_123");
		payment.capture("cap_1");
		
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CAPTURED);
		assertThat(payment.getBankCaptureId()).isEqualTo("cap_1");
	}
	
	@Test
	void cannotCaptureUnauthorizedPayment() {
		Payment payment = newPayment();
		
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
		assertThatThrownBy(() -> payment.capture("cap_1"))
				.isInstanceOf(IllegalStateException.class).hasMessageContaining("CAPTURED");
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
	}
	
	@Test
	void failedTransitionLeavesPaymentUnchanged() {
		Payment payment = newPayment();
		payment.authorize("auth_123");
		payment.capture("cap_1");
		
		assertThatThrownBy(() -> payment.voidPayment("void_1"))
				.isInstanceOf(IllegalStateException.class).hasMessageContaining("CAPTURED");
		
		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CAPTURED);
		assertThat(payment.getBankVoidId()).isNull();
	}
}

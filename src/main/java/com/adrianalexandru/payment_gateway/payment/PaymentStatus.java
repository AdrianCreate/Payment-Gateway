package com.adrianalexandru.payment_gateway.payment;

public enum PaymentStatus {
	PENDING,
	AUTHORIZED,
	CAPTURED,
	VOIDED,
	REFUNDED,
	DECLINED;
	
	public boolean canTransitionTo(PaymentStatus target) {
		return switch (this) {
			case PENDING -> target == AUTHORIZED || target == DECLINED;
			case AUTHORIZED -> target == CAPTURED || target == VOIDED;
			case CAPTURED -> target == REFUNDED;
			case VOIDED, REFUNDED, DECLINED -> false;
		};
	}
}




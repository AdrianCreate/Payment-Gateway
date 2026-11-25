package com.adrianalexandru.payment_gateway.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PaymentStatusTests {

	@ParameterizedTest(name = "{0} -> {1} allowed? {2}")
	@CsvSource({
		// PENDING: can be authorized or declined
		"PENDING,    PENDING,    false",
		"PENDING,    AUTHORIZED, true",
		"PENDING,    CAPTURED,   false",
		"PENDING,    VOIDED,     false",
		"PENDING,    REFUNDED,   false",
		"PENDING,    DECLINED,   true",

		// AUTHORIZED: can be captured or voided
		"AUTHORIZED, PENDING,    false",
		"AUTHORIZED, AUTHORIZED, false",
		"AUTHORIZED, CAPTURED,   true",
		"AUTHORIZED, VOIDED,     true",
		"AUTHORIZED, REFUNDED,   false",
		"AUTHORIZED, DECLINED,   false",

		// CAPTURED: can only be refunded
		"CAPTURED,   PENDING,    false",
		"CAPTURED,   AUTHORIZED, false",
		"CAPTURED,   CAPTURED,   false",
		"CAPTURED,   VOIDED,     false",
		"CAPTURED,   REFUNDED,   true",
		"CAPTURED,   DECLINED,   false",

		// VOIDED: terminal
		"VOIDED,     PENDING,    false",
		"VOIDED,     AUTHORIZED, false",
		"VOIDED,     CAPTURED,   false",
		"VOIDED,     VOIDED,     false",
		"VOIDED,     REFUNDED,   false",
		"VOIDED,     DECLINED,   false",

		// REFUNDED: terminal
		"REFUNDED,   PENDING,    false",
		"REFUNDED,   AUTHORIZED, false",
		"REFUNDED,   CAPTURED,   false",
		"REFUNDED,   VOIDED,     false",
		"REFUNDED,   REFUNDED,   false",
		"REFUNDED,   DECLINED,   false",

		// DECLINED: terminal
		"DECLINED,   PENDING,    false",
		"DECLINED,   AUTHORIZED, false",
		"DECLINED,   CAPTURED,   false",
		"DECLINED,   VOIDED,     false",
		"DECLINED,   REFUNDED,   false",
		"DECLINED,   DECLINED,   false",
	})
	void checksTransition(PaymentStatus from, PaymentStatus to, boolean expected) {
		assertThat(from.canTransitionTo(to)).isEqualTo(expected);
	}
}

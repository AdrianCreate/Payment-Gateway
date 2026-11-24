package com.adrianalexandru.payment_gateway.payment;

import com.adrianalexandru.payment_gateway.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTests {
	
	@Autowired
	private PaymentRepository repository;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	void savesAndLoadsPayment() {
		Payment payment = new Payment("ORD-1", "CUST-1", "4242", 1999);
		
		UUID paymentId = payment.getId();
		repository.save(payment);
		entityManager.flush();
		entityManager.clear();
		
		Payment savedPayment = repository.findById(paymentId).orElseThrow();
		
		assertThat(savedPayment.getOrderId()).isEqualTo("ORD-1");
		assertThat(savedPayment.getAmountCents()).isEqualTo(1999);
		assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
		assertThat(savedPayment.getCustomerId()).isEqualTo("CUST-1");
		assertThat(savedPayment.getCardLast4()).isEqualTo("4242");
		assertThat(savedPayment.getCurrency()).isEqualTo("USD");
		assertThat(savedPayment.getCreatedAt()).isNotNull();
		assertThat(savedPayment.getUpdatedAt()).isNotNull();
		assertThat(savedPayment.getVersion()).isEqualTo(0);
	}
	
	@Test
	void findsActivePaymentByOrderId() {
		Payment payment = new Payment("ORD-1", "CUST-1", "4242", 1999);
		
		UUID paymentId = payment.getId();
		repository.save(payment);
		entityManager.flush();
		entityManager.clear();
		
		Payment foundPayment = repository.findPaymentByOrderIdAndStatusNot("ORD-1", PaymentStatus.DECLINED).orElseThrow();
		// TODO: add declined payment once state transitions exist
		assertThat(foundPayment.getId()).isEqualTo(paymentId);
	}
}

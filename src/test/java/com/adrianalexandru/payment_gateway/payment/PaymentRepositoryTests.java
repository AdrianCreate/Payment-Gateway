package com.adrianalexandru.payment_gateway.payment;

import com.adrianalexandru.payment_gateway.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

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
		Payment firstPayment = new Payment("ORD-1", "CUST-1", "4242", 1999);
		firstPayment.decline();
		repository.save(firstPayment);
		
		Payment secondPayment = new Payment("ORD-1", "CUST-1", "4242", 1999);
		secondPayment.authorize("auth_2");
		UUID paymentId = secondPayment.getId();
		repository.save(secondPayment);
		
		entityManager.flush();
		entityManager.clear();
		
		Payment foundPayment = repository.findPaymentByOrderIdAndStatusNot("ORD-1", PaymentStatus.DECLINED).orElseThrow();
		assertThat(foundPayment.getId()).isEqualTo(paymentId);
	}
	
	@Test
	void concurrentCaptureIsRejectedByVersionCheck() {
		Payment payment = new Payment("ORD-1", "CUST-1", "4242", 1999);
		payment.authorize("auth_1");
		
		repository.save(payment);
		entityManager.flush();
		entityManager.clear();
		
		Payment copyA = repository.findById(payment.getId()).orElseThrow();
		entityManager.clear();
		Payment copyB = repository.findById(payment.getId()).orElseThrow();
		entityManager.clear();
		
		copyA.capture("cap_A");
		repository.saveAndFlush(copyA);
		copyB.capture("cap_B");
		assertThatThrownBy(() -> repository.saveAndFlush(copyB))
				.isInstanceOf(ObjectOptimisticLockingFailureException.class);
		entityManager.clear();
		Payment reloaded = repository.findById(payment.getId()).orElseThrow();
		assertThat(reloaded.getBankCaptureId()).isEqualTo("cap_A");
		assertThat(reloaded.getVersion()).isEqualTo(1);
		assertThat(reloaded.getStatus()).isEqualTo(PaymentStatus.CAPTURED);
	}
}

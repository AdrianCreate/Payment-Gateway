package com.adrianalexandru.payment_gateway.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
	Optional<Payment> findPaymentByOrderIdAndStatusNot(String orderId, PaymentStatus status);
	
	List<Payment> findAllByCustomerIdOrderByCreatedAtDesc(String customerId);
}

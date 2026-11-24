package com.adrianalexandru.payment_gateway.payment;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
	@Id
	@Column(name = "id", nullable = false)
	private UUID id;
	@Column(name = "order_id", nullable = false)
	private String orderId;
	@Column(name = "customer_id", nullable = false)
	private String customerId;
	@Column(name = "amount_cents", nullable = false)
	private long amountCents;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PaymentStatus status;
	@Version
	@Column(name = "version", nullable = false)
	private Integer version;
	@Column(name = "currency", nullable = false)
	private String currency;
	@Column(name = "bank_authorization_id")
	private String bankAuthorizationId;
	@Column(name = "bank_capture_id")
	private String bankCaptureId;
	@Column(name = "bank_void_id")
	private String bankVoidId;
	@Column(name = "bank_refund_id")
	private String bankRefundId;
	@Column(name = "card_last4")
	private String cardLast4;
	@Column(name = "created_at", nullable = false)
	@CreationTimestamp
	private Instant createdAt;
	@Column(name = "updated_at", nullable = false)
	@UpdateTimestamp
	private Instant updatedAt;
	@Column(name = "authorized_at")
	private Instant authorizedAt;
	@Column(name = "captured_at")
	private Instant capturedAt;
	@Column(name = "voided_at")
	private Instant voidedAt;
	@Column(name = "refunded_at")
	private Instant refundedAt;
	
	public Payment(String orderId, String customerId, String cardLast4, long amountCents) {
		this.id = UUID.randomUUID();
		this.status = PaymentStatus.PENDING;
		this.currency = "USD";
		this.orderId = orderId;
		this.customerId = customerId;
		this.cardLast4 = cardLast4;
		this.amountCents = amountCents;
	}
	
	protected Payment() {
	}
	
	public UUID getId() {
		return id;
	}
	
	public String getOrderId() {
		return orderId;
	}
	
	public String getCustomerId() {
		return customerId;
	}
	
	public long getAmountCents() {
		return amountCents;
	}
	
	public Integer getVersion() {
		return version;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public String getBankAuthorizationId() {
		return bankAuthorizationId;
	}
	
	public String getBankCaptureId() {
		return bankCaptureId;
	}
	
	public String getBankVoidId() {
		return bankVoidId;
	}
	
	public String getBankRefundId() {
		return bankRefundId;
	}
	
	public String getCardLast4() {
		return cardLast4;
	}
	
	public Instant getCreatedAt() {
		return createdAt;
	}
	
	public Instant getUpdatedAt() {
		return updatedAt;
	}
	
	public Instant getAuthorizedAt() {
		return authorizedAt;
	}
	
	public Instant getCapturedAt() {
		return capturedAt;
	}
	
	public Instant getVoidedAt() {
		return voidedAt;
	}
	
	public Instant getRefundedAt() {
		return refundedAt;
	}
	
	public PaymentStatus getStatus() {
		return status;
	}
}

CREATE TABLE payments
(
    id                    UUID PRIMARY KEY,
    order_id              TEXT        NOT NULL,
    customer_id           TEXT        NOT NULL,
    amount_cents          BIGINT      NOT NULL,
    currency              TEXT        NOT NULL DEFAULT 'USD',
    status                TEXT        NOT NULL,
    version               INT         NOT NULL DEFAULT 0,
    bank_authorization_id TEXT,
    bank_capture_id       TEXT,
    bank_void_id          TEXT,
    bank_refund_id        TEXT,
    card_last4            TEXT,
    created_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at            TIMESTAMPTZ NOT NULL DEFAULT now(),
    authorized_at         TIMESTAMPTZ,
    captured_at           TIMESTAMPTZ,
    voided_at             TIMESTAMPTZ,
    refunded_at           TIMESTAMPTZ,

    CONSTRAINT payments_status_check
        CHECK (status IN ('PENDING', 'AUTHORIZED', 'CAPTURED', 'VOIDED', 'REFUNDED', 'DECLINED')),
    CONSTRAINT payments_amount_cents_positive
        CHECK (amount_cents > 0)
);

CREATE UNIQUE INDEX payments_order_id_active_uidx
    ON payments (order_id)
    WHERE status <> 'DECLINED';

CREATE INDEX payments_customer_id_idx
    ON payments (customer_id);

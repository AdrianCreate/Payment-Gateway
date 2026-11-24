# Mock bank behaviour notes

Results from calling the mock bank by hand (Swagger UI, `http://localhost:8787/docs`).

## Errors

| Scenario | HTTP | `error` | Whose fault | Retry? |
|----------|------|---------|-------------|--------|
| Random chaos failure | 500 | `internal_error` | Bank (temporary) | **Yes**, with the same key |
| Card has no money (`5555…4444`) | 402 | `insufficient_funds` | Customer | No |
| Card expired (`5105…5100`) | 400 | `card_expired` | Customer | No |
| Wrong CVV | 400 | `invalid_cvv` | Customer | No |
| Capture an already captured auth | 400 | `authorization_already_used` | Gateway (state bug) | No |
| Void an already captured auth | 400 | `authorization_already_used` | Gateway (state bug) | No |
| Capture amount ≠ authorized amount | 400 | `amount_mismatch` | Gateway (bug) | No, log it |
| Malformed JSON (e.g. `09`) | 400 | none (plain-text body) | Whoever sent it | No |

## Idempotency

- Same key, same request → same `authorization_id`. No second hold is created.
- Same body, **new** key → a **new** authorization (a second $50 hold).
- A 500 is not saved against the key, so retrying with the same key processes the request for real (500 → 200).
- So the gateway must generate the key once per operation, store it **before** calling the bank, and reuse it on every retry.

## Takeaways for the gateway

- Decide based on the `error` field, not the HTTP status (declines come back as 400 or 402).
- Some error bodies aren't JSON (`text/plain`), so parsing must not crash.
- An authorization can be used only once (capture **or** void). The gateway should enforce its own state machine and reject invalid transitions before calling the bank.
- Latency is 100–2000 ms per call, so bank calls need timeouts.

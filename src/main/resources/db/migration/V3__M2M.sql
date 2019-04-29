CREATE TABLE payment_account_to_professional_user
(
  PAYMENT_ACCOUNT      UUID,
  PROFESSIONAL_USER    UUID,
  UNIQUE (PAYMENT_ACCOUNT, PROFESSIONAL_USER),
  FOREIGN KEY (PAYMENT_ACCOUNT) REFERENCES payment_account (id),
  FOREIGN KEY (PROFESSIONAL_USER) REFERENCES professional_user (id)
);

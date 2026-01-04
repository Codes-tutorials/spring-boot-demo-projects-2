package com.example.stripe.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PaymentRequest {
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.50", message = "Amount must be at least $0.50")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency = "usd";
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private String paymentMethodId;
    
    private boolean confirmPayment = true;

    public PaymentRequest() {}

    public PaymentRequest(String customerEmail, BigDecimal amount, String currency) {
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.currency = currency;
    }

    // Getters and Setters
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public boolean isConfirmPayment() { return confirmPayment; }
    public void setConfirmPayment(boolean confirmPayment) { this.confirmPayment = confirmPayment; }

    public long getAmountInCents() {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "customerEmail='" + customerEmail + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                ", confirmPayment=" + confirmPayment +
                '}';
    }
}
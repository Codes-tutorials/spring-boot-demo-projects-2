package com.example.stripe.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class RefundRequest {
    
    @NotBlank(message = "Payment Intent ID is required")
    private String paymentIntentId;
    
    @DecimalMin(value = "0.01", message = "Refund amount must be at least $0.01")
    private BigDecimal amount;
    
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    private boolean reverseTransfer = false;
    
    private boolean refundApplicationFee = false;

    public RefundRequest() {}

    public RefundRequest(String paymentIntentId, BigDecimal amount, String reason) {
        this.paymentIntentId = paymentIntentId;
        this.amount = amount;
        this.reason = reason;
    }

    // Getters and Setters
    public String getPaymentIntentId() { return paymentIntentId; }
    public void setPaymentIntentId(String paymentIntentId) { this.paymentIntentId = paymentIntentId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isReverseTransfer() { return reverseTransfer; }
    public void setReverseTransfer(boolean reverseTransfer) { this.reverseTransfer = reverseTransfer; }

    public boolean isRefundApplicationFee() { return refundApplicationFee; }
    public void setRefundApplicationFee(boolean refundApplicationFee) { this.refundApplicationFee = refundApplicationFee; }

    public Long getAmountInCents() {
        return amount != null ? amount.multiply(BigDecimal.valueOf(100)).longValue() : null;
    }

    @Override
    public String toString() {
        return "RefundRequest{" +
                "paymentIntentId='" + paymentIntentId + '\'' +
                ", amount=" + amount +
                ", reason='" + reason + '\'' +
                ", reverseTransfer=" + reverseTransfer +
                ", refundApplicationFee=" + refundApplicationFee +
                '}';
    }
}
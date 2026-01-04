package com.example.stripe.dto;

import jakarta.validation.constraints.*;

public class SubscriptionRequest {
    
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @NotBlank(message = "Price ID is required")
    private String priceId;
    
    @NotBlank(message = "Plan name is required")
    private String planName;
    
    private String paymentMethodId;
    
    private Integer trialPeriodDays;
    
    private String couponId;
    
    private boolean prorationBehavior = true;

    public SubscriptionRequest() {}

    public SubscriptionRequest(String customerEmail, String priceId, String planName) {
        this.customerEmail = customerEmail;
        this.priceId = priceId;
        this.planName = planName;
    }

    // Getters and Setters
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getPriceId() { return priceId; }
    public void setPriceId(String priceId) { this.priceId = priceId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }

    public Integer getTrialPeriodDays() { return trialPeriodDays; }
    public void setTrialPeriodDays(Integer trialPeriodDays) { this.trialPeriodDays = trialPeriodDays; }

    public String getCouponId() { return couponId; }
    public void setCouponId(String couponId) { this.couponId = couponId; }

    public boolean isProrationBehavior() { return prorationBehavior; }
    public void setProrationBehavior(boolean prorationBehavior) { this.prorationBehavior = prorationBehavior; }

    @Override
    public String toString() {
        return "SubscriptionRequest{" +
                "customerEmail='" + customerEmail + '\'' +
                ", priceId='" + priceId + '\'' +
                ", planName='" + planName + '\'' +
                ", trialPeriodDays=" + trialPeriodDays +
                ", prorationBehavior=" + prorationBehavior +
                '}';
    }
}
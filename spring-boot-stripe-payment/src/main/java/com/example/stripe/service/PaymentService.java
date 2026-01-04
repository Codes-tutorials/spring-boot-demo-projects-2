package com.example.stripe.service;

import com.example.stripe.dto.PaymentRequest;
import com.example.stripe.entity.Customer;
import com.example.stripe.entity.Payment;
import com.example.stripe.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private CustomerService customerService;

    public Payment createPaymentIntent(PaymentRequest request) throws StripeException {
        logger.info("Creating payment intent for: {}", request);
        
        // Get or create customer
        Customer customer = customerService.getOrCreateCustomer(
                request.getCustomerEmail(), 
                extractFirstName(request.getCustomerEmail()),
                extractLastName(request.getCustomerEmail())
        );
        
        // Create payment intent in Stripe
        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmountInCents())
                .setCurrency(request.getCurrency())
                .setCustomer(customer.getStripeCustomerId())
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                );
        
        if (request.getDescription() != null) {
            paramsBuilder.setDescription(request.getDescription());
        }
        
        if (request.getPaymentMethodId() != null) {
            paramsBuilder.setPaymentMethod(request.getPaymentMethodId());
        }
        
        PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());
        logger.info("Created Stripe payment intent: {}", paymentIntent.getId());
        
        // Save payment in database
        Payment payment = new Payment(
                paymentIntent.getId(),
                customer,
                request.getAmount(),
                request.getCurrency()
        );
        payment.setDescription(request.getDescription());
        payment.setStatus(mapStripeStatusToPaymentStatus(paymentIntent.getStatus()));
        
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Saved payment to database: {}", savedPayment.getId());
        
        // Confirm payment if requested and payment method is provided
        if (request.isConfirmPayment() && request.getPaymentMethodId() != null) {
            return confirmPayment(paymentIntent.getId());
        }
        
        return savedPayment;
    }

    public Payment confirmPayment(String paymentIntentId) throws StripeException {
        logger.info("Confirming payment intent: {}", paymentIntentId);
        
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        
        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setReturnUrl("http://localhost:8080/success")
                .build();
        
        PaymentIntent confirmedPaymentIntent = paymentIntent.confirm(params);
        logger.info("Confirmed payment intent: {}", confirmedPaymentIntent.getId());
        
        // Update payment in database
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentIntentId));
        
        payment.setStatus(mapStripeStatusToPaymentStatus(confirmedPaymentIntent.getStatus()));
        
        if ("succeeded".equals(confirmedPaymentIntent.getStatus())) {
            payment.setPaidAt(LocalDateTime.now());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        logger.info("Updated payment status: {}", updatedPayment.getStatus());
        
        return updatedPayment;
    }

    @Transactional(readOnly = true)
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Payment> findByStripePaymentIntentId(String paymentIntentId) {
        return paymentRepository.findByStripePaymentIntentId(paymentIntentId);
    }

    @Transactional(readOnly = true)
    public List<Payment> findByCustomer(Customer customer) {
        return paymentRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    @Transactional(readOnly = true)
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Payment> findByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    public Payment updatePaymentFromWebhook(String paymentIntentId, String stripeStatus) {
        logger.info("Updating payment from webhook - ID: {}, Status: {}", paymentIntentId, stripeStatus);
        
        Optional<Payment> paymentOpt = paymentRepository.findByStripePaymentIntentId(paymentIntentId);
        if (paymentOpt.isEmpty()) {
            logger.warn("Payment not found for webhook update: {}", paymentIntentId);
            return null;
        }
        
        Payment payment = paymentOpt.get();
        Payment.PaymentStatus newStatus = mapStripeStatusToPaymentStatus(stripeStatus);
        
        if (payment.getStatus() != newStatus) {
            payment.setStatus(newStatus);
            
            if ("succeeded".equals(stripeStatus) && payment.getPaidAt() == null) {
                payment.setPaidAt(LocalDateTime.now());
            }
            
            Payment updatedPayment = paymentRepository.save(payment);
            logger.info("Updated payment status from webhook: {} -> {}", payment.getStatus(), newStatus);
            return updatedPayment;
        }
        
        return payment;
    }

    private Payment.PaymentStatus mapStripeStatusToPaymentStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> Payment.PaymentStatus.SUCCEEDED;
            case "processing", "requires_payment_method", "requires_confirmation", 
                 "requires_action", "requires_capture" -> Payment.PaymentStatus.PENDING;
            case "canceled" -> Payment.PaymentStatus.CANCELED;
            default -> Payment.PaymentStatus.FAILED;
        };
    }

    private String extractFirstName(String email) {
        String localPart = email.split("@")[0];
        return localPart.split("\\.")[0];
    }

    private String extractLastName(String email) {
        String localPart = email.split("@")[0];
        String[] parts = localPart.split("\\.");
        return parts.length > 1 ? parts[1] : "User";
    }

    @Transactional(readOnly = true)
    public Double getTotalAmountByCustomer(Customer customer) {
        return paymentRepository.getTotalAmountByCustomer(customer);
    }

    public void cancelPayment(String paymentIntentId) throws StripeException {
        logger.info("Canceling payment intent: {}", paymentIntentId);
        
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntent canceledPaymentIntent = paymentIntent.cancel();
        
        // Update payment in database
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentIntentId));
        
        payment.setStatus(Payment.PaymentStatus.CANCELED);
        paymentRepository.save(payment);
        
        logger.info("Canceled payment: {}", paymentIntentId);
    }
}
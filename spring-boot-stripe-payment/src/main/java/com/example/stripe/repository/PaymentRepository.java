package com.example.stripe.repository;

import com.example.stripe.entity.Customer;
import com.example.stripe.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
    
    List<Payment> findByCustomer(Customer customer);
    
    List<Payment> findByCustomerOrderByCreatedAtDesc(Customer customer);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.customer = ?1 AND p.status = ?2")
    List<Payment> findByCustomerAndStatus(Customer customer, Payment.PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN ?1 AND ?2")
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCEEDED' AND p.customer = ?1")
    Double getTotalAmountByCustomer(Customer customer);
}
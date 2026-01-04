package com.example.stripe.repository;

import com.example.stripe.entity.Payment;
import com.example.stripe.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    
    Optional<Refund> findByStripeRefundId(String stripeRefundId);
    
    List<Refund> findByPayment(Payment payment);
    
    List<Refund> findByStatus(Refund.RefundStatus status);
    
    @Query("SELECT r FROM Refund r WHERE r.payment.customer.id = ?1")
    List<Refund> findByCustomerId(Long customerId);
    
    @Query("SELECT r FROM Refund r WHERE r.createdAt BETWEEN ?1 AND ?2")
    List<Refund> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT SUM(r.amount) FROM Refund r WHERE r.status = 'SUCCEEDED' AND r.payment = ?1")
    Double getTotalRefundAmountByPayment(Payment payment);
}
package com.example.stripe.repository;

import com.example.stripe.entity.Customer;
import com.example.stripe.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
    
    List<Subscription> findByCustomer(Customer customer);
    
    List<Subscription> findByCustomerOrderByCreatedAtDesc(Customer customer);
    
    List<Subscription> findByStatus(Subscription.SubscriptionStatus status);
    
    @Query("SELECT s FROM Subscription s WHERE s.customer = ?1 AND s.status IN ('ACTIVE', 'TRIALING')")
    List<Subscription> findActiveSubscriptionsByCustomer(Customer customer);
    
    @Query("SELECT s FROM Subscription s WHERE s.currentPeriodEnd < ?1 AND s.status = 'ACTIVE'")
    List<Subscription> findSubscriptionsEndingBefore(LocalDateTime date);
    
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.status = 'ACTIVE'")
    Long countActiveSubscriptions();
    
    @Query("SELECT s FROM Subscription s WHERE s.customer = ?1 AND s.stripePriceId = ?2 AND s.status IN ('ACTIVE', 'TRIALING')")
    Optional<Subscription> findActiveSubscriptionByCustomerAndPriceId(Customer customer, String priceId);
}
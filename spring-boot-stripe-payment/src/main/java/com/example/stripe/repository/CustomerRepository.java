package com.example.stripe.repository;

import com.example.stripe.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    Optional<Customer> findByStripeCustomerId(String stripeCustomerId);
    
    boolean existsByEmail(String email);
    
    boolean existsByStripeCustomerId(String stripeCustomerId);
    
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.subscriptions WHERE c.id = ?1")
    Optional<Customer> findByIdWithSubscriptions(Long id);
    
    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.payments WHERE c.id = ?1")
    Optional<Customer> findByIdWithPayments(Long id);
}
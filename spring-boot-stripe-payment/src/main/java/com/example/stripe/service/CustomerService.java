package com.example.stripe.service;

import com.example.stripe.entity.Customer;
import com.example.stripe.repository.CustomerRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    
    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(String email, String firstName, String lastName) throws StripeException {
        logger.info("Creating customer: {} {} ({})", firstName, lastName, email);
        
        // Check if customer already exists
        Optional<Customer> existingCustomer = customerRepository.findByEmail(email);
        if (existingCustomer.isPresent()) {
            logger.info("Customer already exists with email: {}", email);
            return existingCustomer.get();
        }
        
        // Create customer in Stripe
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .setName(firstName + " " + lastName)
                .putMetadata("firstName", firstName)
                .putMetadata("lastName", lastName)
                .build();
        
        com.stripe.model.Customer stripeCustomer = com.stripe.model.Customer.create(params);
        logger.info("Created Stripe customer: {}", stripeCustomer.getId());
        
        // Save customer in database
        Customer customer = new Customer(email, firstName, lastName);
        customer.setStripeCustomerId(stripeCustomer.getId());
        
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Saved customer to database: {}", savedCustomer.getId());
        
        return savedCustomer;
    }

    public Customer updateCustomer(Long customerId, String firstName, String lastName) throws StripeException {
        logger.info("Updating customer: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        
        // Update in Stripe
        if (customer.getStripeCustomerId() != null) {
            CustomerUpdateParams params = CustomerUpdateParams.builder()
                    .setName(firstName + " " + lastName)
                    .putMetadata("firstName", firstName)
                    .putMetadata("lastName", lastName)
                    .build();
            
            com.stripe.model.Customer.retrieve(customer.getStripeCustomerId()).update(params);
            logger.info("Updated Stripe customer: {}", customer.getStripeCustomerId());
        }
        
        // Update in database
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        
        Customer updatedCustomer = customerRepository.save(customer);
        logger.info("Updated customer in database: {}", updatedCustomer.getId());
        
        return updatedCustomer;
    }

    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> findByStripeCustomerId(String stripeCustomerId) {
        return customerRepository.findByStripeCustomerId(stripeCustomerId);
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findByIdWithSubscriptions(Long id) {
        return customerRepository.findByIdWithSubscriptions(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
    }

    @Transactional(readOnly = true)
    public Customer findByIdWithPayments(Long id) {
        return customerRepository.findByIdWithPayments(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
    }

    public void deleteCustomer(Long customerId) throws StripeException {
        logger.info("Deleting customer: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));
        
        // Delete from Stripe
        if (customer.getStripeCustomerId() != null) {
            com.stripe.model.Customer stripeCustomer = com.stripe.model.Customer.retrieve(customer.getStripeCustomerId());
            stripeCustomer.delete();
            logger.info("Deleted Stripe customer: {}", customer.getStripeCustomerId());
        }
        
        // Delete from database
        customerRepository.delete(customer);
        logger.info("Deleted customer from database: {}", customerId);
    }

    public Customer getOrCreateCustomer(String email, String firstName, String lastName) throws StripeException {
        Optional<Customer> existingCustomer = findByEmail(email);
        if (existingCustomer.isPresent()) {
            return existingCustomer.get();
        }
        return createCustomer(email, firstName, lastName);
    }

    public void syncCustomersFromStripe() throws StripeException {
        logger.info("Syncing customers from Stripe");
        
        CustomerListParams params = CustomerListParams.builder()
                .setLimit(100L)
                .build();
        
        CustomerCollection customers = com.stripe.model.Customer.list(params);
        
        for (com.stripe.model.Customer stripeCustomer : customers.getData()) {
            Optional<Customer> existingCustomer = findByStripeCustomerId(stripeCustomer.getId());
            
            if (existingCustomer.isEmpty()) {
                String firstName = stripeCustomer.getMetadata().get("firstName");
                String lastName = stripeCustomer.getMetadata().get("lastName");
                
                if (firstName == null || lastName == null) {
                    String[] nameParts = stripeCustomer.getName() != null ? 
                            stripeCustomer.getName().split(" ", 2) : new String[]{"Unknown", "User"};
                    firstName = nameParts[0];
                    lastName = nameParts.length > 1 ? nameParts[1] : "";
                }
                
                Customer customer = new Customer(stripeCustomer.getEmail(), firstName, lastName);
                customer.setStripeCustomerId(stripeCustomer.getId());
                customerRepository.save(customer);
                
                logger.info("Synced customer from Stripe: {}", stripeCustomer.getId());
            }
        }
    }
}
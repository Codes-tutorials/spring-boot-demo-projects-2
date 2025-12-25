package com.example.redis.config;

import com.example.redis.entity.User;
import com.example.redis.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing sample data...");

        // Check if users already exist
        if (userService.findAll().isEmpty()) {
            // Create sample users
            User user1 = new User("admin", "admin@example.com", "Admin", "User");
            User user2 = new User("demo_user", "demo@example.com", "Demo", "User");
            User user3 = new User("test_user", "test@example.com", "Test", "User");

            userService.save(user1);
            userService.save(user2);
            userService.save(user3);

            logger.info("Sample users created successfully");
        } else {
            logger.info("Sample data already exists, skipping initialization");
        }
    }
}
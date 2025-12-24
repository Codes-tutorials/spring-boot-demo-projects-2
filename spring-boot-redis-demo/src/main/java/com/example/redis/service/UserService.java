package com.example.redis.service;

import com.example.redis.entity.User;
import com.example.redis.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private UserRepository userRepository;

    @Cacheable(value = "users", key = "#id")
    public Optional<User> findById(Long id) {
        logger.info("Fetching user from database with id: {}", id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setLastAccessTime(LocalDateTime.now());
            userRepository.save(user.get());
        }
        return user;
    }

    @Cacheable(value = "users", key = "#username")
    public Optional<User> findByUsername(String username) {
        logger.info("Fetching user from database with username: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            user.get().setLastAccessTime(LocalDateTime.now());
            userRepository.save(user.get());
        }
        return user;
    }

    @CachePut(value = "users", key = "#result.id")
    public User save(User user) {
        logger.info("Saving user to database: {}", user.getUsername());
        return userRepository.save(user);
    }

    @CachePut(value = "users", key = "#user.id")
    public User update(User user) {
        logger.info("Updating user in database: {}", user.getUsername());
        return userRepository.save(user);
    }

    @CacheEvict(value = "users", key = "#id")
    public void deleteById(Long id) {
        logger.info("Deleting user from database with id: {}", id);
        userRepository.deleteById(id);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void deleteAll() {
        logger.info("Deleting all users from database and clearing cache");
        userRepository.deleteAll();
    }

    public List<User> findAll() {
        logger.info("Fetching all users from database");
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
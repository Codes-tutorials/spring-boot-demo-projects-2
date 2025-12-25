package com.example.redis.service;

import com.example.redis.entity.User;
import com.example.redis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        cacheManager.getCache("users").clear();
    }

    @Test
    void testSaveAndFindById() {
        // Given
        User user = new User("testuser", "test@example.com", "Test", "User");

        // When
        User savedUser = userService.save(user);
        Optional<User> foundUser = userService.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void testFindByUsername() {
        // Given
        User user = new User("testuser", "test@example.com", "Test", "User");
        userService.save(user);

        // When
        Optional<User> foundUser = userService.findByUsername("testuser");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void testCaching() {
        // Given
        User user = new User("cacheduser", "cached@example.com", "Cached", "User");
        User savedUser = userService.save(user);

        // When - First call should hit database
        Optional<User> firstCall = userService.findById(savedUser.getId());
        
        // Second call should hit cache
        Optional<User> secondCall = userService.findById(savedUser.getId());

        // Then
        assertTrue(firstCall.isPresent());
        assertTrue(secondCall.isPresent());
        assertEquals(firstCall.get().getUsername(), secondCall.get().getUsername());
    }

    @Test
    void testUpdate() {
        // Given
        User user = new User("originaluser", "original@example.com", "Original", "User");
        User savedUser = userService.save(user);

        // When
        savedUser.setFirstName("Updated");
        savedUser.setLastName("Name");
        User updatedUser = userService.update(savedUser);

        // Then
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("Name", updatedUser.getLastName());
    }

    @Test
    void testDeleteById() {
        // Given
        User user = new User("deleteuser", "delete@example.com", "Delete", "User");
        User savedUser = userService.save(user);

        // When
        userService.deleteById(savedUser.getId());
        Optional<User> foundUser = userService.findById(savedUser.getId());

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testExistsByUsername() {
        // Given
        User user = new User("existsuser", "exists@example.com", "Exists", "User");
        userService.save(user);

        // When & Then
        assertTrue(userService.existsByUsername("existsuser"));
        assertFalse(userService.existsByUsername("nonexistentuser"));
    }

    @Test
    void testExistsByEmail() {
        // Given
        User user = new User("emailuser", "email@example.com", "Email", "User");
        userService.save(user);

        // When & Then
        assertTrue(userService.existsByEmail("email@example.com"));
        assertFalse(userService.existsByEmail("nonexistent@example.com"));
    }
}
package com.example.crud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrphanRemovalTest {
  @Autowired
  private AuthorRepository authorRepository;
  @Autowired
  private BookRepository bookRepository;

  @Test
  @Transactional
  void removingBookFromAuthorDeletesIt() {
    Author a = new Author();
    a.setName("Alice");
    Book b1 = new Book();
    b1.setTitle("One");
    Book b2 = new Book();
    b2.setTitle("Two");
    a.addBook(b1);
    a.addBook(b2);
    Author saved = authorRepository.save(a);
    Long b1Id = b1.getId();
    Long b2Id = b2.getId();
    saved.removeBook(b1);
    authorRepository.save(saved);
    assertFalse(bookRepository.existsById(b1Id));
    assertTrue(bookRepository.existsById(b2Id));
  }
}


package com.example.crud;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
  private final BookRepository bookRepository;

  public BookController(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @GetMapping
  public List<Book> list() {
    return bookRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Book> get(@PathVariable Long id) {
    return bookRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Book create(@RequestBody Book book) {
    return bookRepository.save(book);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Book> update(@PathVariable Long id, @RequestBody Book book) {
    return bookRepository.findById(id).map(existing -> {
      existing.setTitle(book.getTitle());
      existing.setAuthor(book.getAuthor());
      return ResponseEntity.ok(bookRepository.save(existing));
    }).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!bookRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    bookRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}


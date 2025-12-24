package com.example.crud;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {
  private final AuthorRepository authorRepository;

  public AuthorController(AuthorRepository authorRepository) {
    this.authorRepository = authorRepository;
  }

  @GetMapping
  public List<Author> list() {
    return authorRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Author> get(@PathVariable Long id) {
    return authorRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public Author create(@RequestBody Author author) {
    return authorRepository.save(author);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Author> update(@PathVariable Long id, @RequestBody Author author) {
    return authorRepository.findById(id).map(existing -> {
      existing.setName(author.getName());
      if (author.getBooks() != null) {
        existing.setBooks(author.getBooks());
      }
      return ResponseEntity.ok(authorRepository.save(existing));
    }).orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    if (!authorRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    authorRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/books")
  public ResponseEntity<Book> addBook(@PathVariable Long id, @RequestBody Book book) {
    return authorRepository.findById(id).map(author -> {
      author.addBook(book);
      authorRepository.save(author);
      return ResponseEntity.ok(book);
    }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}/books/{bookId}")
  public ResponseEntity<?> removeBookFromAuthor(@PathVariable Long id, @PathVariable Long bookId) {
    return authorRepository.findById(id).map(author -> {
      Book b = author.getBooks().stream().filter(x -> bookId.equals(x.getId())).findFirst().orElse(null);
      if (b == null) {
        return ResponseEntity.notFound().build();
      }
      author.removeBook(b);
      authorRepository.save(author);
      return ResponseEntity.noContent().build();
    }).orElseGet(() -> ResponseEntity.notFound().build());
  }
}

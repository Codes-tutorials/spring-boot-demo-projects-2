package com.example.crud.aopdemo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/aop/accounts")
public class AccountController {
  private final AccountRepository repository;
  private final AccountService service;

  public AccountController(AccountRepository repository, AccountService service) {
    this.repository = repository;
    this.service = service;
  }

  @GetMapping
  public List<Account> list() {
    return repository.findAll();
  }

  @PostMapping
  public Account create(@RequestParam String owner, @RequestParam(required = false) BigDecimal initial) {
    return service.create(owner, initial);
  }

  @PostMapping("/transfer")
  public ResponseEntity<Void> transfer(@RequestParam Long from, @RequestParam Long to, @RequestParam BigDecimal amount) {
    service.transfer(from, to, amount);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/boom")
  public String boom() {
    throw new RuntimeException("Boom");
  }
}


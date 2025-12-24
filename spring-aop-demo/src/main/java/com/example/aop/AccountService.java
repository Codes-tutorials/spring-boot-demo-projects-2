package com.example.aop;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class AccountService {
  private final AccountRepository repository;

  public AccountService(AccountRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public void transfer(Long fromId, Long toId, BigDecimal amount) {
    if (amount == null || amount.signum() <= 0) {
      throw new IllegalArgumentException("Invalid amount");
    }
    Account from = repository.findById(fromId).orElseThrow(() -> new IllegalArgumentException("Source not found"));
    Account to = repository.findById(toId).orElseThrow(() -> new IllegalArgumentException("Target not found"));
    if (from.getBalance().compareTo(amount) < 0) {
      throw new IllegalArgumentException("Insufficient funds");
    }
    from.setBalance(from.getBalance().subtract(amount));
    to.setBalance(to.getBalance().add(amount));
    repository.save(from);
    repository.save(to);
  }

  public Account create(String owner, BigDecimal initial) {
    Account a = new Account();
    a.setOwner(owner);
    a.setBalance(initial == null ? BigDecimal.ZERO : initial);
    return repository.save(a);
  }
}


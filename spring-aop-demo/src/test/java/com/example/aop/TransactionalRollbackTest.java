package com.example.aop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionalRollbackTest {
  @Autowired
  private AccountService service;
  @Autowired
  private AccountRepository repository;

  @Test
  @Transactional
  void insufficientFundsRollsBack() {
    Account a = service.create("A", new BigDecimal("10.00"));
    Account b = service.create("B", new BigDecimal("0.00"));
    assertThrows(IllegalArgumentException.class, () -> service.transfer(a.getId(), b.getId(), new BigDecimal("15.00")));
    Account reloadedA = repository.findById(a.getId()).orElseThrow();
    Account reloadedB = repository.findById(b.getId()).orElseThrow();
    assertEquals(new BigDecimal("10.00"), reloadedA.getBalance());
    assertEquals(new BigDecimal("0.00"), reloadedB.getBalance());
  }
}

